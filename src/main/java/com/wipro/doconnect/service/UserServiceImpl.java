package com.wipro.doconnect.service;

/**
*
*@author kunal
*/

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wipro.doconnect.dto.AskQuestionDTO;
import com.wipro.doconnect.dto.PostAnswerDTO;
import com.wipro.doconnect.entity.Answer;
import com.wipro.doconnect.entity.Question;
import com.wipro.doconnect.entity.User;
import com.wipro.doconnect.exception.AlreadyThere;
import com.wipro.doconnect.exception.NotFound;
import com.wipro.doconnect.repository.IAnswerRepo;
import com.wipro.doconnect.repository.IQuestionRepo;
import com.wipro.doconnect.repository.IUserRepo;

@Service
public class UserServiceImpl implements IUserService {

	@Autowired
	private IUserRepo userRepo;

	@Autowired
	private IQuestionRepo questionRepo;

	@Autowired
	private IAnswerRepo answerRepo;

	@Autowired
	private EntityManager entityManager;

	@Override
	public User userLogin(String email, String password) {

		User user = userRepo.findByEmail(email);
		if (Objects.isNull(user))
			throw new NotFound();

		if (user.getPassword().equals(password)) {
			user.setIsActive(true);
			userRepo.save(user);
		} else
			throw new NotFound();
		return user;
	}

	@Override
	public String userLogout(Long userId) {

		User user = userRepo.findById(userId).orElseThrow(() -> new NotFound("User Not Found" + userId));
		user.setIsActive(false);
		userRepo.save(user);

		return "Logged Out";
	}

	@Override
	public User userRegister(User user) {

		User user1 = userRepo.findByEmail(user.getEmail());
		System.out.println(user1);
		if (Objects.isNull(user1)) {
			System.out.println(Objects.isNull(user1));
			return userRepo.save(user);
		}
		throw new AlreadyThere();
	}

	@Override
	public Question askQuestion(AskQuestionDTO askQuestionDTO) {
		Question question = new Question();

		User user = userRepo.findById(askQuestionDTO.getUserId()).orElseThrow(() -> new NotFound("User Not Found"));
		question.setQuestion(askQuestionDTO.getQuestion());
		question.setTopic(askQuestionDTO.getTopic());
		question.setUser(user);
		questionRepo.save(question);
		return question;
	}

	@Override
	public Answer giveAnswer(@Valid PostAnswerDTO postAnswerDTO) {
		Answer answer = new Answer();
		User answerUser = userRepo.findById(postAnswerDTO.getUserId())
				.orElseThrow(() -> new NotFound("User Not Found"));

		Question question = questionRepo.findById(postAnswerDTO.getQuestionId())
				.orElseThrow(() -> new NotFound("Question Not Found"));
		answer.setQuestion(question);
		answer.setAnswer(postAnswerDTO.getAnswer());
		answer.setAnswerUser(answerUser);

		answerRepo.save(answer);
		return answer;
	}

	@Override
	public List<Question> searchQuestion(String question) {

		String sqlQuery = "from Question where (question like :question) and isApproved = 1";
		return entityManager.createQuery(sqlQuery, Question.class).setParameter("question", "%" + question + "%")
				.getResultList();
	}

	@Override
	public List<Answer> getAnswers(Long questionId) {
		return answerRepo.findByQuestionId(questionId);
	}

	@Override
	public List<Question> getQuestions(String topic) {
		if (topic.equalsIgnoreCase("All")) {
			return questionRepo.findByIsApprovedTrue();
		}
		return questionRepo.findByTopicAndApproved(topic);
	}

}
