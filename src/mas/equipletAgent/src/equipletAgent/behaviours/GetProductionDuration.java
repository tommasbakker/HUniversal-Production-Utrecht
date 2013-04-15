/**
 * @file GetProductionDuration.java
 * @brief Behaviour for handling the messages with the ontology GetProductionDuration
 * @date Created: 2013-04-02
 *
 * @author Hessel Meulenbeld
 *
 * @section LICENSE
 * License: newBSD
 *
 * Copyright � 2013, HU University of Applied Sciences Utrecht.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the HU University of Applied Sciences Utrecht nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED SCIENCES UTRECHT
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package equipletAgent.behaviours;

import org.bson.types.ObjectId;

import behaviours.ReceiveBehaviour;

import equipletAgent.EquipletAgent;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * The Class GetProductionDuration.
 */
public class GetProductionDuration extends ReceiveBehaviour {
	/**
	 * @var static final long serialVersionUID
	 * The serial version UID for this class
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * @var MessageTemplate messageTemplate
	 * The messageTemplate this behaviour listens to.
	 * This behaviour listens to the ontology: GetProductionDuration.
	 */
	private static MessageTemplate messageTemplate = MessageTemplate.MatchOntology("GetProductionDuration");
	
	/**
	 * @var EquipletAgent equipletAgent
	 * The equipletAgent related to this behaviour.
	 */
	private EquipletAgent equipletAgent;

	/**
	 * Instantiates a new gets the production duration.
	 * 
	 * @param a The agent for this behaviour
	 * 
	 */
	public GetProductionDuration(Agent a) {
		super(a, -1, messageTemplate);
		equipletAgent = (EquipletAgent) a;
	}

	/**
	 * Function to handle the incoming messages for this behaviour.
	 * Handles the response to the GetProductionDuration question and asks the service agent the same question.
	 * 
	 * @param message - The received message.
	 */
	@Override
	public void handle(ACLMessage message) {
		System.out.format("%s received message from %s%n", myAgent.getLocalName(), message.getSender().getLocalName(), message.getOntology());
		try {
			ObjectId productStepId = equipletAgent.getCommunicationSlot(message.getConversationId());
			ACLMessage responseMessage = new ACLMessage(ACLMessage.REQUEST);
			responseMessage.addReceiver(equipletAgent.getServiceAgent());
			responseMessage.setConversationId(message.getConversationId());
			responseMessage.setContentObject(productStepId);
			responseMessage.setOntology("GetProductionStepDuration");
			myAgent.send(responseMessage);
		} catch (Exception e) {
			// TODO: ERROR HANDLING
			e.printStackTrace();
			myAgent.doDelete();
		}
	}
}
