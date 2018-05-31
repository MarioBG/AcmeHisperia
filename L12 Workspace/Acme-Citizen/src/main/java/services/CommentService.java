
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import repositories.CommentRepository;
import domain.Comment;
import forms.CommentForm;

@Service
@Transactional
public class CommentService {

	// Managed repository

	@Autowired
	private CommentRepository	commentRepository;

	// Supporting services

	@Autowired
	private ActorService		actorService;

	@Autowired
	private CommentableService	commentableService;

	@Autowired
	private Validator			validator;


	// Constructors

	public CommentService() {
		super();
	}

	// Simple CRUD methods

	public Comment create(final int commentableId, final Integer parentCommentId) {

		final Comment res = new Comment();

		res.setMoment(new Date(System.currentTimeMillis() - 1000));

		res.setActor(this.actorService.findByPrincipal());
		res.setCommentable(this.commentableService.findOne(commentableId));
		if (parentCommentId != null)
			res.setParentComment(this.findOne(parentCommentId));
		res.setReplies(new ArrayList<Comment>());

		return res;
	}
	public Collection<Comment> findAll() {
		Collection<Comment> res;
		res = this.commentRepository.findAll();
		Assert.notNull(res);
		return res;
	}

	public Comment findOne(final int commentId) {
		Assert.isTrue(commentId != 0);
		Comment res;
		res = this.commentRepository.findOne(commentId);
		Assert.notNull(res);
		return res;
	}

	public Comment save(final Comment comment) {

		Assert.notNull(comment);

		if (comment.getId() == 0)
			comment.setMoment(new Date(System.currentTimeMillis() - 1000));

		final Comment res = this.commentRepository.save(comment);

		if (comment.getId() == 0) {
			res.getActor().getComments().add(res);
			res.getCommentable().getComments().add(res);
			if (res.getParentComment() != null)
				res.getParentComment().getReplies().add(res);
		}

		return res;
	}

	public void delete(final Comment comment) {

		comment.getCommentable().getComments().remove(comment);
		comment.getActor().getComments().remove(comment);
		if (comment.getParentComment() != null)
			comment.getParentComment().getReplies().remove(comment);

		final Collection<Comment> replies = new ArrayList<Comment>(comment.getReplies());
		for (final Comment c : replies)
			this.delete(c);

		this.commentRepository.delete(comment);
	}

	// Other business methods

	public CommentForm construct(final Comment comment) {

		Assert.notNull(comment);

		CommentForm commentForm;

		commentForm = new CommentForm();

		commentForm.setId(comment.getId());
		commentForm.setParentCommentId(comment.getParentComment().getId());
		commentForm.setActorId(comment.getActor().getId());
		commentForm.setCommentableId(comment.getCommentable().getId());
		commentForm.setMoment(comment.getMoment());
		commentForm.setText(comment.getText());
		commentForm.setPicture(comment.getPicture());

		return commentForm;
	}

	public Comment reconstruct(final CommentForm commentForm, final BindingResult binding) {

		Assert.notNull(commentForm);

		Comment comment;

		if (commentForm.getId() != 0)
			comment = this.findOne(commentForm.getId());
		else
			comment = this.create(commentForm.getCommentableId(), commentForm.getParentCommentId());

		comment.setMoment(commentForm.getMoment());
		comment.setText(commentForm.getText());
		comment.setPicture(commentForm.getPicture());

		if (binding != null)
			this.validator.validate(comment, binding);

		return comment;
	}

	public void flush() {
		this.commentRepository.flush();
	}

}
