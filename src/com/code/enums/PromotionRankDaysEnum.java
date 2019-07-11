package com.code.enums;

public enum PromotionRankDaysEnum {
     
    BRIGADIER(1800),                // 3amed
    COLONEL(1620),                  // 3aqeed
    LIEUTENANT_COLONEL(1440),       // Moqadem
    MAJOR(1440),                    // Raed
    CAPTAIN(2160),                  // Naqeeb
    FIRST_LIEUTENANT(1440),         // Molazem awal
    LIEUTENANT(720),                // Molazem
    
    PRIME_SERGEANTS(1800),          // Raaes roabaa
    STAFF_SERGEANT(1800),           // Raqeeb awal
    SERGEANT(1440),                 // Raqeeb
    UNDER_SERGEANT(1080),           // Wakeel raqeeb
    CORPORAL(1080),                 // 3areef
    FIRST_SOLDIER(720),             // Gondy awal
    SOLDIER(720),                   // Gondy
    
    STAFF_SERGEANT_FEMALE(1800),           // mofatesha Raqeeb awal
    SERGEANT_FEMALE(1440),                 // mofatesha Raqeeb
    UNDER_SERGEANT_FEMALE(1080),           // mofatesha Wakeel raqeeb
    CORPORAL_FEMALE(1080),          // mofatesha 3areef
    FIRST_SOLDIER_FEMALE(720),      // mofatesha Gondy awal
    SOLDIER_FEMALE(720),            // mofatesha Gondy
    
    THIRTEENTH(720),
    TWELFTH(720),
    ELEVENTH(720),
    TENTH(720),
    NINTH(1440),
    EIGHTH(1440),
    SEVENTH(1440),
    SIXTH(1440),
    FIFTH(1440),
    FOURTH(1440),
    THIRD(1440),
    SECOND(1440),
    FIRST(1440);
 
    private int code;

    private PromotionRankDaysEnum(int code) {
	this.code = code;
    }

    public int getCode() {
	return code;
    }
}
