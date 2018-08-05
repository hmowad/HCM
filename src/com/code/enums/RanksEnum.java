package com.code.enums;

public enum RanksEnum {
    /* Officers ranks */
    FIRST_LIEUTENANT_GENERAL(101), // farek awal
    LIEUTENANT_GENERAL(102),       // farek
    MAJOR_GENERAL(103),            // lwa2
    BRIGADIER(104),                // 3amed
    COLONEL(105),                  // 3aqeed
    LIEUTENANT_COLONEL(106),       // Moqadem
    MAJOR(107),                    // Raed
    CAPTAIN(108),                  // Naqeeb
    FIRST_LIEUTENANT(109),         // Molazem awal
    LIEUTENANT(110),               // Molazem
    CADET(112),                    // Taleb Dabbet
    
    /* Soldiers ranks */
    PRIME_SERGEANTS(201),          // Raaes roabaa
    STAFF_SERGEANT(202),           //Raqeeb awal
    SERGEANT(203),                 // Raqeeb
    UNDER_SERGEANT(204),           // Wakeel raqeeb
    CORPORAL(205),                 //3areef
    FIRST_SOLDIER(206),            // Gondy awal
    SOLDIER(207),                  // Gondy
    STUDENT_SOLDIER(210),          // Taleb frd

    /* Employees ranks */
    FIFTEENTH(301),
    FOURTEENTH(302),
    THIRTEENTH(303),
    TWELFTH(304),
    ELEVENTH(305),
    TENTH(306),
    NINTH(307),
    EIGHTH(308),
    SEVENTH(309),
    SIXTH(310),
    FIFTH(311),
    FOURTH(312),
    THIRD(313),
    SECOND(314),
    FIRST(315),
    
    /* Users ranks */
    THIRTY_THIRD(401),
    THIRTY_SECOND(402),
    THIRTY_ONE(403),
    
    /* Wages ranks */
    WORKER_D(501),
    WORKER_C(502),
    WORKER_B(503),
    WORKER_A(504),
    
    /* Contractors ranks */
    CONTRACTOR_SIXTEENTH(601),
    CONTRACTOR_FIFTEENTH(602),
    CONTRACTOR_FOURTEENTH(603),
    CONTRACTOR_THIRTEENTH(604),
    CONTRACTOR_TWELFTH(605),
    CONTRACTOR_ELEVENTH(606),
    CONTRACTOR_TENTH(607),
    CONTRACTOR_NINTH(608),
    CONTRACTOR_EIGHTH(609),
    CONTRACTOR_SEVENTH(610),
    CONTRACTOR_SIXTH(611),
    CONTRACTOR_FIFTH(612),
    CONTRACTOR_FOURTH(613),
    CONTRACTOR_THIRD(614),
    CONTRACTOR_SECOND(615),
    CONTRACTOR_FIRST(616);
    
    private long code;

    private RanksEnum(long code) {
	this.code = code;
    }

    public long getCode() {
	return code;
    }
}
