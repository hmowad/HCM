package com.code.enums;

public enum WFProcessesEnum {

    // ------------------------Vacations-----------------------------------------
    OFFICERS_VACATION_JOINING(1),
    SOLDIERS_VACATION_JOINING(2),
    CIVILIANS_VACATION_JOINING(3),

    OFFICERS_REGULAR_VACATION(5),
    MODIFY_OFFICERS_REGULAR_VACATION(10),
    CANCEL_OFFICERS_REGULAR_VACATION(15),

    SOLDIERS_REGULAR_VACATION(20),
    MODIFY_SOLDIERS_REGULAR_VACATION(25),
    CANCEL_SOLDIERS_REGULAR_VACATION(30),

    EMPLOYEES_REGULAR_VACATION(35),
    MODIFY_EMPLOYEES_REGULAR_VACATION(40),
    CANCEL_EMPLOYEES_REGULAR_VACATION(45),

    OFFICERS_COMPELLING_VACATION(50),
    CANCEL_OFFICERS_COMPELLING_VACATION(55),

    SOLDIERS_COMPELLING_VACATION(60),
    CANCEL_SOLDIERS_COMPELLING_VACATION(65),

    EMPLOYEES_COMPELLING_VACATION(70),
    CANCEL_EMPLOYEES_COMPELLING_VACATION(75),

    CONTRACTORS_WITHOUT_SALARY_VACATION(80),
    CANCEL_CONTRACTORS_WITHOUT_SALARY_VACATION(85),

    OFFICERS_SICK_VACATION(100),
    MODIFY_OFFICERS_SICK_VACATION(105),
    CANCEL_OFFICERS_SICK_VACATION(110),

    SOLDIERS_SICK_VACATION(115),
    MODIFY_SOLDIERS_SICK_VACATION(120),
    CANCEL_SOLDIERS_SICK_VACATION(125),

    SOLDIERS_WORK_INJURY_SICK_VACATION(126),
    MODIFY_SOLDIERS_WORK_INJURY_SICK_VACATION(127),
    CANCEL_SOLDIERS_WORK_INJURY_SICK_VACATION(128),

    EMPLOYEES_SICK_VACATION(130),
    MODIFY_EMPLOYEES_SICK_VACATION(135),
    CANCEL_EMPLOYEES_SICK_VACATION(140),

    OFFICERS_FIELD_VACATION(150),
    CANCEL_OFFICERS_FIELD_VACATION(151),

    SOLDIERS_FIELD_VACATION(152),
    CANCEL_SOLDIERS_FIELD_VACATION(153),

    OFFICERS_EXCEPTIONAL_VACATION(154),
    CANCEL_OFFICERS_EXCEPTIONAL_VACATION(155),

    SOLDIERS_EXCEPTIONAL_VACATION(156),
    CANCEL_SOLDIERS_EXCEPTIONAL_VACATION(157),

    EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION(158),
    CANCEL_EMPLOYEES_EXCEPTIONAL_FOR_CIRCUMSTANCES_VACATION(159),

    EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION(160),
    CANCEL_EMPLOYEES_EXCEPTIONAL_FOR_ACCOMPANY_VACATION(161),

    EMPLOYEES_ACCOMPANY_VACATION(162),
    CANCEL_EMPLOYEES_ACCOMPANY_VACATION(163),

    SOLDIERS_EXAM_VACATION(164),
    CANCEL_SOLDIERS_EXAM_VACATION(165),

    EMPLOYEES_EXAM_VACATION(166),
    CANCEL_EMPLOYEES_EXAM_VACATION(167),

    SOLDIERS_MATERNITY_VACATION(168),
    CANCEL_SOLDIERS_MATERNITY_VACATION(169),

    EMPLOYEES_MATERNITY_VACATION(170),
    CANCEL_EMPLOYEES_MATERNITY_VACATION(171),

    SOLDIERS_MOTHERHOOD_VACATION(172),
    CANCEL_SOLDIERS_MOTHERHOOD_VACATION(173),

    EMPLOYEES_MOTHERHOOD_VACATION(174),
    CANCEL_EMPLOYEES_MOTHERHOOD_VACATION(175),

    SOLDIERS_DEATH_WAITING_PERIOD_VACATION(176),
    CANCEL_SOLDIERS_DEATH_WAITING_PERIOD_VACATION(177),

    EMPLOYEES_DEATH_WAITING_PERIOD_VACATION(178),
    CANCEL_EMPLOYEES_DEATH_WAITING_PERIOD_VACATION(179),

    EMPLOYEES_STUDY_VACATION(180),
    CANCEL_EMPLOYEES_STUDY_VACATION(181),

    EMPLOYEES_RELIEF_VACATION(182),
    CANCEL_EMPLOYEES_RELIEF_VACATION(183),

    EMPLOYEES_SPORTIVE_VACATION(184),
    CANCEL_EMPLOYEES_SPORTIVE_VACATION(185),

    EMPLOYEES_CULTURAL_VACATION(186),
    CANCEL_EMPLOYEES_CULTURAL_VACATION(187),

    EMPLOYEES_SOCIAL_VACATION(188),
    CANCEL_EMPLOYEES_SOCIAL_VACATION(189),

    EMPLOYEES_COMPENSATION_VACATION(190),
    CANCEL_EMPLOYEES_COMPENSATION_VACATION(191),

    EMPLOYEES_ORPHAN_CARE_VACATION(192),
    CANCEL_EMPLOYEES_ORPHAN_CARE_VACATION(193),

    EMPLOYEES_DEATH_OF_RELATIVE_VACATION(194),
    CANCEL_EMPLOYEES_DEATH_OF_RELATIVE_VACATION(195),

    EMPLOYEES_NEW_BABY_VACATION(196),
    CANCEL_EMPLOYEES_NEW_BABY_VACATION(197),

    // --------------------------------------------------------------------------

    DEFINITION_LETTERS(200),

    OFFICERS_RECRUITMENT(300),
    SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_RECRUITMENT(310),
    SOLDIERS_CORPORAL_OR_HIGHER_RECRUITMENT(311),
    SOLDIERS_GRADUATION_LETTER_DECISION_RECRUITMENT(312),
    SOLDIERS_SOLDIER_OR_FIRST_SOLDIER_EXCEPTIONAL_RECRUITMENT(313),
    SOLDIERS_CORPORAL_OR_HIGHER_EXCEPTIONAL_RECRUITMENT(314),
    SOLDIERS_GRADUATION_LETTER_DECISION_EXCEPTIONAL_RECRUITMENT(315),
    INSPECTORS_EXCEPTIONAL_RECRUITMENT(316),
    INSPECTORS_RECRUITMENT(317),
    PERSONS_RECRUITMENT(320),
    USERS_RECRUITMENT(330),
    WAGES_RECRUITMENT(340),
    CONTRACTORS_RECRUITMENT(350),
    MEDICAL_STAFF_RECRUITMENT(360),

    OFFICERS_MOVE(400),
    OFFICERS_SUBJOIN(401),
    OFFICERS_SUBJOIN_EXTENSION(402),
    OFFICERS_SUBJOIN_TERMINATION(403),
    OFFICERS_SUBJOIN_CANCELLATION(404),
    OFFICERS_ASSIGNMENT(405),

    SOLDIERS_MOVE(407),
    SOLDIERS_SUBJOIN(408),
    SOLDIERS_SUBJOIN_EXTENSION(409),
    SOLDIERS_SUBJOIN_TERMINATION(410),
    SOLDIERS_SUBJOIN_CANCELLATION(411),
    SOLDIERS_MANDATE(412),
    SOLDIERS_MANDATE_EXTENSION(413),
    SOLDIERS_MANDATE_TERMINATION(414),
    SOLDIERS_MANDATE_CANCELLATION(415),
    SOLDIERS_SECONDMENT(416),
    SOLDIERS_SECONDMENT_EXTENSION(417),
    SOLDIERS_SECONDMENT_TERMINATION(418),
    SOLDIERS_SECONDMENT_CANCELLATION(419),

    PERSONS_MOVE(421),
    PERSONS_ASSIGNMENT(422),
    PERSONS_ASSIGNMENT_EXTENSION(423),
    PERSONS_ASSIGNMENT_TERMINATION(424),
    PERSONS_ASSIGNMENT_CANCELLATION(425),

    OFFICERS_MOVE_REQUEST(426),
    OFFICERS_SUBJOIN_REQUEST(427),
    OFFICERS_SUBJOIN_EXTENSION_REQUEST(428),
    OFFICERS_SUBJOIN_TERMINATION_REQUEST(429),
    OFFICERS_SUBJOIN_CANCELLATION_REQUEST(430),

    SOLDIERS_MOVE_REQUEST(431),
    SOLDIERS_SUBJOIN_REQUEST(432),
    SOLDIERS_SUBJOIN_EXTENSION_REQUEST(433),
    SOLDIERS_SUBJOIN_TERMINATION_REQUEST(434),
    SOLDIERS_SUBJOIN_CANCELLATION_REQUEST(435),

    PERSONS_MOVE_REQUEST(436),
    PERSONS_ASSIGNMENT_REQUEST(437),
    PERSONS_ASSIGNMENT_EXTENSION_REQUEST(438),
    PERSONS_ASSIGNMENT_TERMINATION_REQUEST(439),
    PERSONS_ASSIGNMENT_CANCELLATION_REQUEST(440),

    SOLDIERS_MOVE_WISH_REQUEST(450),
    SOLDIERS_MOVE_WISH_MODIFY_REQUEST(451),
    SOLDIERS_MOVE_WISH_CANCEL_REQUEST(452),
    SOLDIERS_MOVE_WISH(453),
    SOLDIERS_MOVE_WISH_MODIFY(454),
    SOLDIERS_MOVE_WISH_CANCEL(455),
    OFFICERS_MOVE_JOINING_DATE_REQUEST(456),
    SOLDIERS_MOVE_JOINING_DATE_REQUEST(457),
    OFFICERS_SUBJOIN_JOINING_DATE_REQUEST(458),
    SOLDIERS_SUBJOIN_JOINING_DATE_REQUEST(459),
    OFFICERS_SUBJOIN_RETURN_JOINING_DATE_REQUEST(460),

    OFFICERS_MISSION(810),
    SOLDIERS_MISSION(820),
    CIVILIANS_MISSION(830),

    OFFICERS_PROMOTION(905),
    SOLDIERS_PROMOTION(910),
    SOLDIERS_EXCEPTIONAL_PROMOTION(920),
    CIVILIANS_PROMOTION(930),
    SOLDIERS_PROMOTION_CANCELLATION(940),

    // ------------------------ Training And Scholarship -----------------------------------------

    // WFTrainingPlanData
    TRAINING_PLAN_INITIATION(1000),
    TRAINING_PLAN_FINALIZATION(1002),
    TRAINING_PLAN_APPROVAL(1003),

    // WFTrainingPlanNeedData
    TRAINING_PLAN_NEEDS_REQUEST(1001),

    // WFTrainingCourseEventData
    MILITARY_INTERNAL_COURSE_EVENT_REQUEST(1004),
    MILITARY_INTERNAL_COURSE_EVENT_POSTPONE_REQUEST(1005),
    MILITARY_INTERNAL_COURSE_EVENT_CANCEL_REQUEST(1006),
    MILITARY_INTERNAL_COURSE_EVENT_RESULTS(1013),
    MILITARY_INTERNAL_COURSE_EVENT_MODIFY_REQUEST(1037),

    // WFTrainingData
    MILITARY_INTERNAL_TRAINING_REQUEST(1007),
    MILITARY_INTERNAL_TRAINING_APOLOGY_REQUEST(1008),
    MILITARY_INTERNAL_TRAINING_REPLACEMENT_REQUEST(1009),
    MILITARY_EXTERNAL_TRAINING_REQUEST(1010),
    MILITARY_EXTERNAL_TRAINING_APOLOGY_REQUEST(1011),
    MILITARY_EXTERNAL_TRAINING_REPLACEMENT_REQUEST(1012),

    EMPLOYEE_MILITARY_INTERNAL_COURSE_EVENT_CANCEL(1014),

    MORNING_TRAINING_REQUEST(1015),
    EVENING_TRAINING_REQUEST(1016),
    STUDY_ENROLLMENT_REQUEST(1017),
    SCHOLARSHIP_REQUEST(1018),
    SCHOLARSHIP(1019),
    SCHOLARSHIP_EXTENSION(1020),
    SCHOLARSHIP_TERMINATION(1021),
    SCHOLARSHIP_CANCEL(1022),

    INTERNAL_MILITARY_TRAINING_CLAIM_REQUEST(1023),
    EXTERNAL_MILITARY_TRAINING_CLAIM_REQUEST(1024),
    EVENING_TRAINING_CLAIM_REQUEST(1025),
    STUDY_ENROLLMENT_CLAIM_REQUEST(1026),
    MORNING_TRAINING_CLAIM_REQUEST(1027),
    SCHOLARSHIP_CLAIM_REQUEST(1028),
    INTERNAL_MILITARY_TRAINING_CLAIM(1029),
    EXTERNAL_MILITARY_TRAINING_CLAIM(1030),
    EVENING_TRAINING_CLAIM(1031),
    STUDY_ENROLLMENT_CLAIM(1032),
    MORNING_TRAINING_CLAIM(1033),
    SCHOLARSHIP_CLAIM(1034),

    // WFTrainingCourseName
    ADD_MILITARY_TRAINING_COURSE_NAME_REQUEST(1035),
    ADD_CIVILLAIN_TRAINING_COURSE_NAME_REQUEST(1036),

    MILITARY_TRAINING_COURSE_SYLLABUS_APPROVE(1038),
    MILITARY_TRAINING_COURSE_SYLLABUS_CANCEL(1039),

    // ------------------------Manpower Needs-----------------------------------------
    OFFICERS_MANPOWER_NEED_REQUEST(710),
    SOLDIERS_MANPOWER_NEED_REQUEST(720),

    OFFICERS_MANPOWER_NEED_STUDY(730),
    SOLDIERS_MANPOWER_NEED_STUDY(740),

    // ------------------------Service Termination-----------------------------------------
    OFFICERS_TERMINATION_REQUEST(500),

    SOLDIERS_TERMINATION_REQUEST(510),
    SOLDIERS_TERMINATION_RECORD(520),
    SOLDIERS_TERMINATION_CANCELLATION(530),

    CONTRACTORS_TERMINATION_REQUEST(540),
    CIVILIANS_TERMINATION_REQUEST(550),
    CIVILIANS_TERMINATION_RECORD(560),
    CIVILIANS_TERMINATION_CANCELLATION(570),
    CIVILIANS_TERMINATION_MOVEMENT(580),

    // ------------------------Retirement-----------------------------------------
    OFFICERS_DISCLAIMER_REQUEST(1700),
    SOLDIERS_DISCLAIMER_REQUEST(1710),
    EXTENSION_REQUEST(1720),
    REEXTENSION_REQUEST(1730),

    // ------------------------Organization Jobs-------------------------------------------
    OFFICERS_JOBS_TRANSACTIONS(1510),
    SOLDIERS_JOBS_TRANSACTIONS(1520),

    SOLDIERS_JOBS_RESERVATION(1530),
    SOLDIERS_JOBS_UNRESERVATION(1540),

    // ---------------------------General Notifications------------------------------------------
    NOTIFICATION_MESSAGE(2000),
    GENERAL_MESSAGE(2010),

    // ---------------------------ESERVICES------------------------------------------
    ESERVICES_NOTIFICATIONS(100);

    private long code;

    private WFProcessesEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
