package com.google.sampling.experiential.server.migration.jobs;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.sampling.experiential.datastore.ExperimentJsonEntityManager;
import com.google.sampling.experiential.model.Experiment;
import com.google.sampling.experiential.model.Input;
import com.google.sampling.experiential.model.SignalSchedule;
import com.google.sampling.experiential.model.Trigger;
import com.google.sampling.experiential.server.ExperimentAccessManager;
import com.google.sampling.experiential.server.ExperimentRetrieverOld;
import com.google.sampling.experiential.server.migration.MigrationJob;
import com.google.sampling.experiential.server.stats.participation.ParticipationStatsService;
import com.pacoapp.paco.shared.model.SignalScheduleDAO;
import com.pacoapp.paco.shared.model.TriggerDAO;
import com.pacoapp.paco.shared.model2.ActionTrigger;
import com.pacoapp.paco.shared.model2.ExperimentDAO;
import com.pacoapp.paco.shared.model2.ExperimentGroup;
import com.pacoapp.paco.shared.model2.ExperimentValidator;
import com.pacoapp.paco.shared.model2.Feedback;
import com.pacoapp.paco.shared.model2.Input2;
import com.pacoapp.paco.shared.model2.InterruptCue;
import com.pacoapp.paco.shared.model2.InterruptTrigger;
import com.pacoapp.paco.shared.model2.JsonConverter;
import com.pacoapp.paco.shared.model2.PacoAction;
import com.pacoapp.paco.shared.model2.PacoNotificationAction;
import com.pacoapp.paco.shared.model2.Pair;
import com.pacoapp.paco.shared.model2.Schedule;
import com.pacoapp.paco.shared.model2.ScheduleTrigger;
import com.pacoapp.paco.shared.model2.SignalTime;
import com.pacoapp.paco.shared.model2.ValidationMessage;

public class EventStatsCounterMigrationJob implements MigrationJob {

  public static final Logger log = Logger.getLogger(EventStatsCounterMigrationJob.class.getName());

  
    public boolean doMigrationTaskQueue() {
    log.info("STARTING EventStats Counter MIGRATION");
    
    Queue taskQueue = QueueFactory.getQueue("migration");
        
    taskQueue.add(TaskOptions.Builder
                  .withTaskName("EventStatsMigrationMaster")
                  .param("jobId", "EventStatsMigration"));
    
    log.info("LEAVING EventStats Counter MIGRATION");
    return true;
  }
  

    public boolean doMigrationPerExperimentPerUser() {
      // for each experiment
      //get all events 
      // or get counts by unique grp, exp, date, who
      // split them up by user,group,day, count stats across all events
      // insert that stat record for each user
      return true;
      
    }
    
    
    // Everything after 2016/03/04 09:11:07 PST
  public boolean doMigrationInLoopForLatestOnly() {
    System.out.println("STARTING MIGRATION eventStats in loop" );
    final int limit = 30;

    Cursor cursor = null;
    boolean firstPass = true;

    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    ParticipationStatsService ps = new ParticipationStatsService();
    
    while (firstPass || cursor != null) {
      firstPass = false;
      
      Query query = new Query("Event");
      DateTime lastRetrievedDateTime = new DateTime(2016, 03, 04, 9, 11, 07, 0, DateTimeZone.forID("America/Los_Angeles"));
      Date lastRetrievedAsDate = lastRetrievedDateTime.toDate();
      query.setFilter(new com.google.appengine.api.datastore.Query.FilterPredicate("when", FilterOperator.GREATER_THAN, lastRetrievedAsDate));
      
      PreparedQuery q = ds.prepare(query);
      
      FetchOptions options = FetchOptions.Builder.withLimit(limit);
      if (cursor != null) {
        options = options.startCursor(cursor);
      }
      
      QueryResultList<Entity> results = q.asQueryResultList(options);

      for (Entity eventEntity : results) {
        boolean isJoinOrScheduleEvent = false;
        List<String> keysList = (List<String>)eventEntity.getProperty("keysList");
        if (keysList != null && (keysList.contains("joined") || keysList.contains("schedule"))) {
          isJoinOrScheduleEvent = true;
        }
        
        if (!isJoinOrScheduleEvent) {
          String experimentIdStr = (String)eventEntity.getProperty("experimentId");
          Long experimentId = Long.parseLong(experimentIdStr);
          String experimentGroupName = (String)eventEntity.getProperty("experimentGroupName");
          String who = (String)eventEntity.getProperty("who");
          
          Date rt = (Date)eventEntity.getProperty("responseTime");
          Date st = (Date)eventEntity.getProperty("scheduledTime");
          
          if (st != null && rt != null) {
            DateTime dateTime = new DateTime(st);
            log.info(": Updating scheduled response Count for " + experimentId + ", " + experimentGroupName + ", " + who + ", " + dateTime.toString());
            ps.updateScheduledResponseCountForWho(experimentId, experimentGroupName, who, dateTime);
          } else if (st != null && rt == null) {
            DateTime dateTime = new DateTime(st);
            log.info(": Updating missed response Count for " + experimentId + ", " + experimentGroupName + ", " + who + ", " + dateTime.toString());
            ps.updateMissedResponseCountForWho(experimentId, experimentGroupName, who, dateTime);
          } else if (st == null && rt != null) {        
            DateTime dateTimeRt = new DateTime(rt);
            log.info(": Updating selfreport response Count for " + experimentId + ", " + experimentGroupName
                     + ", " + who + ", " + dateTimeRt.toString());
            ps.updateSelfResponseCountForWho(experimentId, experimentGroupName, who, dateTimeRt);
          }
        } else {
          log.info("join event");
        }
      }
      
      if (results.size() < limit) {
        break;
      } else {
        cursor = results.getCursor();
      }
      
    }

    log.severe("Done processing events");

    return true;
  }

  public boolean doMigrationInLoop() {
    System.out.println("STARTING MIGRATION eventStats in loop" );
    final int limit = 30;

    Cursor cursor = null;
    boolean firstPass = true;

    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    ParticipationStatsService ps = new ParticipationStatsService();
    
    while (firstPass || cursor != null) {
      firstPass = false;
      
      PreparedQuery q = ds.prepare(new Query("Event"));
      FetchOptions options = FetchOptions.Builder.withLimit(limit);
      if (cursor != null) {
        options = options.startCursor(cursor);
      }
      
      QueryResultList<Entity> results = q.asQueryResultList(options);

      for (Entity eventEntity : results) {
        boolean isJoinOrScheduleEvent = false;
        List<String> keysList = (List<String>)eventEntity.getProperty("keysList");
        if (keysList != null && (keysList.contains("joined") || keysList.contains("schedule"))) {
          isJoinOrScheduleEvent = true;
        }
        
        if (!isJoinOrScheduleEvent) {
          String experimentIdStr = (String)eventEntity.getProperty("experimentId");
          Long experimentId = Long.parseLong(experimentIdStr);
          String experimentGroupName = (String)eventEntity.getProperty("experimentGroupName");
          String who = (String)eventEntity.getProperty("who");
          
          Date rt = (Date)eventEntity.getProperty("responseTime");
          Date st = (Date)eventEntity.getProperty("scheduledTime");
          
          if (st != null && rt != null) {
            DateTime dateTime = new DateTime(st);
            log.info(": Updating scheduled response Count for " + experimentId + ", " + experimentGroupName + ", " + who + ", " + dateTime.toString());
            ps.updateScheduledResponseCountForWho(experimentId, experimentGroupName, who, dateTime);
          } else if (st != null && rt == null) {
            DateTime dateTime = new DateTime(st);
            log.info(": Updating missed response Count for " + experimentId + ", " + experimentGroupName + ", " + who + ", " + dateTime.toString());
            ps.updateMissedResponseCountForWho(experimentId, experimentGroupName, who, dateTime);
          } else if (st == null && rt != null) {        
            DateTime dateTimeRt = new DateTime(rt);
            log.info(": Updating selfreport response Count for " + experimentId + ", " + experimentGroupName
                     + ", " + who + ", " + dateTimeRt.toString());
            ps.updateSelfResponseCountForWho(experimentId, experimentGroupName, who, dateTimeRt);
          }
        } else {
          log.info("join event");
        }
      }
      
      if (results.size() < limit) {
        break;
      } else {
        cursor = results.getCursor();
      }
      
    }

    log.severe("Done processing events");

    return true;
  }

  public boolean doMigrationWithMapReduce() {
    
    return true;
  }

  @Override
  public boolean doMigration() {
    return doMigrationWithMapReduce();
    //return doMigrationInLoopForLatestOnly();
  }

}
