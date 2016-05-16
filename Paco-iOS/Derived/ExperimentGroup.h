//
//  Generated by the J2ObjC translator.  DO NOT EDIT!
//  source: /Users/northropo/Projects/paco/Shared/src/com/pacoapp/paco/shared/model2/ExperimentGroup.java
//

#include "J2ObjC_header.h"

#pragma push_macro("ExperimentGroup_INCLUDE_ALL")
#ifdef ExperimentGroup_RESTRICT
#define ExperimentGroup_INCLUDE_ALL 0
#else
#define ExperimentGroup_INCLUDE_ALL 1
#endif
#undef ExperimentGroup_RESTRICT

#if !defined (PAExperimentGroup_) && (ExperimentGroup_INCLUDE_ALL || defined(PAExperimentGroup_INCLUDE))
#define PAExperimentGroup_

#define ModelBase_RESTRICT 1
#define PAModelBase_INCLUDE 1
#include "ModelBase.h"

#define Validatable_RESTRICT 1
#define PAValidatable_INCLUDE 1
#include "Validatable.h"

#define JavaIoSerializable_RESTRICT 1
#define JavaIoSerializable_INCLUDE 1
#include "java/io/Serializable.h"

@class JavaLangBoolean;
@class JavaLangInteger;
@class JavaLangLong;
@class PAActionTrigger;
@class PAFeedback;
@protocol JavaUtilList;
@protocol PAValidator;

@interface PAExperimentGroup : PAModelBase < PAValidatable, JavaIoSerializable >

#pragma mark Public

- (instancetype)init;

- (instancetype)initWithNSString:(NSString *)string;

- (PAActionTrigger *)getActionTriggerByIdWithJavaLangLong:(JavaLangLong *)actionTriggerId;

- (id<JavaUtilList>)getActionTriggers;

- (JavaLangBoolean *)getBackgroundListen;

- (NSString *)getBackgroundListenSourceIdentifier;

- (JavaLangBoolean *)getCustomRendering;

- (NSString *)getCustomRenderingCode;

- (NSString *)getEndDate;

- (JavaLangBoolean *)getEndOfDayGroup;

- (NSString *)getEndOfDayReferredGroupName;

- (PAFeedback *)getFeedback;

- (JavaLangInteger *)getFeedbackType;

- (JavaLangBoolean *)getFixedDuration;

- (id<JavaUtilList>)getInputs;

- (JavaLangBoolean *)getLogActions;

- (NSString *)getName;

- (NSString *)getStartDate;

- (void)setActionTriggersWithJavaUtilList:(id<JavaUtilList>)actionTriggers;

- (void)setBackgroundListenWithJavaLangBoolean:(JavaLangBoolean *)backgroundListen;

- (void)setBackgroundListenSourceIdentifierWithNSString:(NSString *)backgroundListenSourceIdentifier;

- (void)setCustomRenderingWithJavaLangBoolean:(JavaLangBoolean *)customRendering;

- (void)setCustomRenderingCodeWithNSString:(NSString *)customRenderingCode;

- (void)setEndDateWithNSString:(NSString *)endDate;

- (void)setEndOfDayGroupWithJavaLangBoolean:(JavaLangBoolean *)endOfDayGroup;

- (void)setEndOfDayReferredGroupNameWithNSString:(NSString *)endOfDayReferredGroupName;

- (void)setFeedbackWithPAFeedback:(PAFeedback *)feedback;

- (void)setFeedbackTypeWithJavaLangInteger:(JavaLangInteger *)feedbackType;

- (void)setFixedDurationWithJavaLangBoolean:(JavaLangBoolean *)fixedDuration;

- (void)setInputsWithJavaUtilList:(id<JavaUtilList>)inputs;

- (void)setLogActionsWithJavaLangBoolean:(JavaLangBoolean *)logActions;

- (void)setNameWithNSString:(NSString *)name;

- (void)setStartDateWithNSString:(NSString *)startDate;

- (void)validateActionTriggersWithPAValidator:(id<PAValidator>)validator;

- (void)validateInputsWithPAValidator:(id<PAValidator>)validator;

- (void)validateWithWithPAValidator:(id<PAValidator>)validator;

@end

J2OBJC_EMPTY_STATIC_INIT(PAExperimentGroup)

FOUNDATION_EXPORT void PAExperimentGroup_init(PAExperimentGroup *self);

FOUNDATION_EXPORT PAExperimentGroup *new_PAExperimentGroup_init() NS_RETURNS_RETAINED;

FOUNDATION_EXPORT PAExperimentGroup *create_PAExperimentGroup_init();

FOUNDATION_EXPORT void PAExperimentGroup_initWithNSString_(PAExperimentGroup *self, NSString *string);

FOUNDATION_EXPORT PAExperimentGroup *new_PAExperimentGroup_initWithNSString_(NSString *string) NS_RETURNS_RETAINED;

FOUNDATION_EXPORT PAExperimentGroup *create_PAExperimentGroup_initWithNSString_(NSString *string);

J2OBJC_TYPE_LITERAL_HEADER(PAExperimentGroup)

@compatibility_alias ComPacoappPacoSharedModel2ExperimentGroup PAExperimentGroup;

#endif

#pragma pop_macro("ExperimentGroup_INCLUDE_ALL")