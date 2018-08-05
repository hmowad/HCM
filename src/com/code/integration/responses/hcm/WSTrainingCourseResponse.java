package com.code.integration.responses.hcm;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.code.dal.orm.hcm.trainings.TrainingCourseData;
import com.code.integration.responses.WSResponseBase;

public class WSTrainingCourseResponse extends WSResponseBase {
    private List<TrainingCourseData> trainingCoursesData;

    @XmlElementWrapper(name = "trainingCourses")
    @XmlElement(name = "trainingCourse")
    public List<TrainingCourseData> getTrainingCoursesData() {
	return trainingCoursesData;
    }

    public void setTrainingCoursesData(List<TrainingCourseData> trainingCoursesData) {
	this.trainingCoursesData = trainingCoursesData;
    }
}
