package com.example.quiz12;

import com.example.quiz12.service.ifs.FeedbackService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Quiz12ApplicationTests {

	@Autowired
	private FeedbackService feedbackService;

	@Test
	void contextLoads() {
		feedbackService.statistics(1);
	}

}
