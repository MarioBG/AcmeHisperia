
package controllers;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import services.BankAgentService;
import domain.Agent;
import forms.BankAgentForm;

@Controller
@RequestMapping("/agent")
public class AgentController extends AbstractController {

	// Services -------------------------------------------------------------

	@Autowired
	private BankAgentService	agentService;


	// Constructors ---------------------------------------------------------

	public AgentController() {
		super();
	}

	// Listing --------------------------------------------------------------

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() {

		ModelAndView result;
		Collection<Agent> agents;
		Agent principal;

		agents = this.agentService.findAll();
		principal = this.agentService.findByPrincipal();
		if (principal != null)
			agents.remove(principal);

		result = new ModelAndView("agent/list");
		result.addObject("agents", agents);
		result.addObject("principal", principal);
		result.addObject("requestURI", "agent/list.do");

		return result;
	}

	@RequestMapping(value = "/display", method = RequestMethod.GET)
	public ModelAndView listUser(@RequestParam final int agentId) {
		ModelAndView result;
		Agent agent;

		agent = this.agentService.findOne(agentId);

		result = new ModelAndView("agent/display");
		result.addObject("agent", agent);
		result.addObject("requestURI", "agent/display.do");

		return result;
	}

	// Register

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView create() {
		ModelAndView res;

		final Agent agent = this.agentService.create();
		final BankAgentForm bankAgentForm = this.agentService.construct(agent);

		res = this.createEditModelAndView(bankAgentForm);

		return res;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid final BankAgentForm bankAgentForm, final BindingResult binding) {
		ModelAndView res;
		Agent agent;

		if (binding.hasErrors())
			res = this.createEditModelAndView(bankAgentForm, "user.params.error");
		else if (!bankAgentForm.getRepeatPassword().equals(bankAgentForm.getPassword()))
			res = this.createEditModelAndView(bankAgentForm, "user.commit.errorPassword");
		else if (bankAgentForm.getTermsAndConditions() == false)
			res = this.createEditModelAndView(bankAgentForm, "user.params.errorTerms");
		else
			try {
				agent = this.agentService.reconstruct(bankAgentForm, binding);
				this.agentService.save(agent);
				res = new ModelAndView("redirect:../");
			} catch (final Throwable oops) {
				res = this.createEditModelAndView(bankAgentForm, "user.commit.error");
			}

		return res;
	}

	// Ancillary methods ---------------------------------------------------------------

	protected ModelAndView createEditModelAndView(final BankAgentForm bankAgentForm) {
		ModelAndView result;

		result = this.createEditModelAndView(bankAgentForm, null);

		return result;
	}

	protected ModelAndView createEditModelAndView(final BankAgentForm bankAgentForm, final String message) {
		ModelAndView result;

		result = new ModelAndView("agent/register");
		result.addObject("agentForm", bankAgentForm);
		result.addObject("message", message);

		return result;
	}

}
