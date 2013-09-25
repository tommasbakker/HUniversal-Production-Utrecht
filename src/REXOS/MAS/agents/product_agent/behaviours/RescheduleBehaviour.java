/**
 *
 * Project: PA-V2
 *
 * Package: rexos.mas.productAgent
 *
 * File: RescheduleBehaviour.java
 *
 * Author: Mike Schaap
 *
 * Version: 1.0
 *
 */
package agents.product_agent.behaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.rmi.server.UID;
import java.util.ArrayList;

import libraries.utillities.log.Logger;
import agents.data.BehaviourStatus;
import agents.data.Product;
import agents.data.Production;
import agents.data.ProductionStep;
import agents.data.StepStatusCode;
import agents.product_agent.BehaviourCallback;
import agents.product_agent.ProductAgent;

/**
 * @author Mike
 *
 */
public class RescheduleBehaviour extends Behaviour {
	private ProductAgent _productAgent;
	private BehaviourCallback _bc;

	public RescheduleBehaviour(Agent a, BehaviourCallback bc) {
		super(a);
		_productAgent = (ProductAgent) a;
		this._bc = bc;
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void action() {
		//System.out.println("RESCHEDULING!");
		Product product = _productAgent.getProduct();
		Production prod = product.getProduction();
		ArrayList<ProductionStep> steps = prod.getProductionSteps();
		
		for(ProductionStep step : steps) {
			if(step.getStatus() != StepStatusCode.DONE && step.getStatus() != StepStatusCode.PLANNED) {
				step.setStatus(StepStatusCode.RESCHEDULE);
				ACLMessage message = new ACLMessage(ACLMessage.INFORM);
				message.addReceiver(step.getUsedEquiplet());
				message.setOntology("AbortStep");
				message.setConversationId(step.getConversationId());
				message.addUserDefinedParameter("message-id", new UID().toString());
				Logger.logAclMessage(message, 's');
				myAgent.send(message);
			}
		}	
		
		prod.setProductionSteps(steps);
		product.setProduction(prod);
		_productAgent.setProduct(product);
		_bc.handleCallback(BehaviourStatus.COMPLETED, null);
	}
	

	/* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#done()
	 */
	@Override
	public boolean done() {
		// TODO Auto-generated method stub
		return true;
	}
	
	
}
