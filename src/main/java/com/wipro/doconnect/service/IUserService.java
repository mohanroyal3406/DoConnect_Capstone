package com.wipro.doconnect.service;

/**
*
*@author kunal
*/

import java.util.List;

import javax.validation.Valid;

import com.wipro.doconnect.dto.AskQuestionDTO;
import com.wipro.doconnect.dto.PostAnswerDTO;
import com.wipro.doconnect.entity.Answer;
import com.wipro.doconnect.entity.Question;
import com.wipro.doconnect.entity.User;

public interface IUserService {

	public User userLogin(String email, String password);

	public String userLogout(Long userId);

	public User userRegister(@Valid User user);

	public Question askQuestion(@Valid AskQuestionDTO askQuestionDTO);

	public Answer giveAnswer(@Valid PostAnswerDTO postAnswerDTO);

	public List<Question> searchQuestion(String question);

	public List<Answer> getAnswers(Long questionId);

	public List<Question> getQuestions(String topic);

}
