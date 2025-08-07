-- Enable file imports (only needed once)
SET GLOBAL local_infile = 1;

-- Grant permissions to allow file uploads
GRANT FILE ON *.* TO 'root'@'localhost';

-- Drop existing tables if they exist
DROP TABLE IF EXISTS section, course, professor, room, building, college, staging_course_schedule;

-- Create a staging table to hold raw data (Permanent, so Java can access it)
CREATE TABLE IF NOT EXISTS staging_course_schedule (
    EVENT_ID VARCHAR(50),
    SECTION VARCHAR(10),
    EVENT_TYPE VARCHAR(20),
    EVENT_LONG_NAME VARCHAR(255),
    ADDS VARCHAR(10),
    MAX_PARTICIPANT VARCHAR(10),
    DAY VARCHAR(50),
    START_TIME VARCHAR(20),  
    END_TIME VARCHAR(20),    
    FIRST_NAME VARCHAR(100),
    LAST_NAME VARCHAR(100),
    POSITION VARCHAR(100),
    CREDITS VARCHAR(10),
    BUILDING_CODE VARCHAR(50),
    ROOM_ID VARCHAR(50),
    COLLEGE VARCHAR(100),
    DEPARTMENT VARCHAR(255),
    START_DATE VARCHAR(20),  
    END_DATE VARCHAR(20)     
);

ALTER TABLE staging_course_schedule
ADD COLUMN HideOnlineSearch VARCHAR(10);

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/CourseScheduleSpringStudentCSVCopy.csv'
INTO TABLE staging_course_schedule
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n'
IGNORE 1 ROWS;

SELECT * FROM staging_course_schedule LIMIT 1;

-- Create normalized tables
CREATE TABLE college (
    College_ID INT AUTO_INCREMENT PRIMARY KEY,
    College_Name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE building (
    Building_ID INT AUTO_INCREMENT PRIMARY KEY,
    Building_Name VARCHAR(255) UNIQUE,
    College_ID INT,
    FOREIGN KEY (College_ID) REFERENCES college(College_ID)
);

CREATE TABLE room (
    Room_ID INT AUTO_INCREMENT PRIMARY KEY,
    Room_Number VARCHAR(50),
    Building_ID INT,
    FOREIGN KEY (Building_ID) REFERENCES building(Building_ID)
);

CREATE TABLE professor (
    Professor_ID INT AUTO_INCREMENT PRIMARY KEY,
    First_Name VARCHAR(100),
    Last_Name VARCHAR(100),
    Position VARCHAR(100),
    College_ID INT,
    Department VARCHAR(100),
    FOREIGN KEY (College_ID) REFERENCES college(College_ID)
);

CREATE TABLE course (
    Course_ID INT AUTO_INCREMENT PRIMARY KEY,
    Course_Number VARCHAR(50),
    Course_Name VARCHAR(255),
    Credits VARCHAR(10),
    Department VARCHAR(255),
    College_ID INT,
    FOREIGN KEY (College_ID) REFERENCES college(College_ID)
);

CREATE TABLE section (
    Section_ID INT AUTO_INCREMENT PRIMARY KEY,
    Course_ID INT,
    Section_Number VARCHAR(10),
    Professor_ID INT,
    Room_ID INT,
    Instruction_Day VARCHAR(50),
    Start_Time VARCHAR(20),
    End_Time VARCHAR(20),
    Max_Participants VARCHAR(10),
    Start_Date VARCHAR(20),
    End_Date VARCHAR(20),
    UNIQUE (Course_ID, Section_Number, Professor_ID, Room_ID),
    FOREIGN KEY (Course_ID) REFERENCES course(Course_ID),
    FOREIGN KEY (Professor_ID) REFERENCES 
    professor(Professor_ID),
    FOREIGN KEY (Room_ID) REFERENCES room(Room_ID)
);

DELIMITER //

CREATE PROCEDURE ImportCSV()
BEGIN
    -- Insert into College
    INSERT INTO college (College_Name)
    SELECT DISTINCT TRIM(COLLEGE)
    FROM staging_course_schedule
    WHERE COLLEGE IS NOT NULL
    ON DUPLICATE KEY UPDATE College_Name = VALUES(College_Name);

    -- Insert into Building
    INSERT INTO building (Building_Name, College_ID)
    SELECT DISTINCT TRIM(BUILDING_CODE), c.College_ID
    FROM staging_course_schedule scs
    LEFT JOIN college c ON TRIM(scs.COLLEGE) = TRIM(c.College_Name)
    WHERE scs.BUILDING_CODE IS NOT NULL
    ON DUPLICATE KEY UPDATE College_ID = VALUES(College_ID);

    -- Insert into Room
    INSERT INTO room (Room_Number, Building_ID)
    SELECT DISTINCT ROOM_ID, b.Building_ID
    FROM staging_course_schedule scs
    LEFT JOIN building b ON TRIM(scs.BUILDING_CODE) = TRIM(b.Building_Name)
    WHERE scs.ROOM_ID IS NOT NULL;

    -- Insert into Professor
    INSERT INTO professor (First_Name, Last_Name, Position, College_ID, Department)
    SELECT DISTINCT FIRST_NAME, LAST_NAME, POSITION, c.College_ID, DEPARTMENT
    FROM staging_course_schedule scs
    LEFT JOIN college c ON TRIM(scs.COLLEGE) = TRIM(c.College_Name)
    WHERE FIRST_NAME IS NOT NULL;

    -- Insert into Course
    INSERT INTO course (Course_Number, Course_Name, Credits, Department, College_ID)
    SELECT DISTINCT EVENT_ID, EVENT_LONG_NAME, CREDITS, DEPARTMENT, c.College_ID
    FROM staging_course_schedule scs
    LEFT JOIN college c ON TRIM(scs.COLLEGE) = TRIM(c.College_Name)
    WHERE EVENT_ID IS NOT NULL;

    -- Insert into Section (with conflict resolution)
    INSERT INTO section (
        Course_ID, Section_Number, Professor_ID, Room_ID,
        Instruction_Day, Start_Time, End_Time,
        Max_Participants, Start_Date, End_Date
    )
    SELECT DISTINCT
        c.Course_ID,
        scs.SECTION,
        p.Professor_ID,
        r.Room_ID,
        scs.DAY,
        scs.START_TIME,
        scs.END_TIME,
        scs.MAX_PARTICIPANT,
        scs.START_DATE,
        scs.END_DATE
    FROM staging_course_schedule scs
    LEFT JOIN course c ON scs.EVENT_ID = c.Course_Number
    LEFT JOIN professor p ON scs.FIRST_NAME = p.First_Name AND scs.LAST_NAME = p.Last_Name
    LEFT JOIN room r ON scs.ROOM_ID = r.Room_Number
    WHERE c.Course_ID IS NOT NULL
    ON DUPLICATE KEY UPDATE
        Instruction_Day = VALUES(Instruction_Day),
        Start_Time = VALUES(Start_Time),
        End_Time = VALUES(End_Time),
        Max_Participants = VALUES(Max_Participants),
        Start_Date = VALUES(Start_Date),
        End_Date = VALUES(End_Date);
END //

DELIMITER ;

SELECT * FROM staging_course_schedule;

SELECT DISTINCT ROOM_ID, BUILDING_CODE FROM staging_course_schedule;