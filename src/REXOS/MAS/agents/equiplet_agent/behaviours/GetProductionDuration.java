/**
 * @file rexos/mas/equiplet_agent/behaviours/GetProductionDuration.java
 * @brief Behaviour for handling the messages with the ontology GetProductStepDuration
 * @date Created: 2013-04-02
 * 
 * @author Hessel Meulenbeld
 * 
 * @section LICENSE
 *          License: newBSD
 * 
 *          Copyright � 2013, HU University of Applied Sciences Utrecht.
 *          All rights reserved.
 * 
 *          Redistribution and use in source and binary forms, with or without modification, are permitted provided that
 *          the following conditions are met:
 *          - Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *          following disclaimer.
 *          - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *          following disclaimer in the documentation and/or other materials provided with the distribution.
 *          - Neither the name of the HU University of Applied Sciences Utrecht nor the names of its contributors may be
 *          used to endorse or promote products derived from this software without specific prior written permission.
 * 
 *          THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *          "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *          THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *          ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED SCIENCES UTRECHT
 *          BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *          CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 *          GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *          HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *          LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 *          OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package agents.equiplet_agent.behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.rmi.server.UID;

import libraries.utillities.log.LogLevel;
import libraries.utillities.log.Logger;

import org.bson.types.ObjectId;

import agents.equiplet_agent.EquipletAgent;
import agents.shared_behaviours.ReceiveBehaviour;

/**
 * The receive behaviour for receiving messages with ontology: "GetProductionDuration".
 * When a message is received it checks if it can handle it.
 * If not it sends a message to the sender with ontology:"ConversationIdUnknown".
 * When it can handle the message, it sends a message to the serviceAgent with ontology: "GetProductionStepDuration".
 */
public class GetProductionDuration extends ReceiveBehaviour {
	/**
	 * @var static final long serialVersionUID
	 *      The serial version UID for this class
	 */
	private static final long serialVersionUID = 2502684423295372637L;

	/**
	 * @var MessageTemplate messageTemplate
	 *      The messageTemplate this behaviour listens to.
	 *      This behaviour listens to the ontology: GetProductStepDuration.
	 */
	private static MessageTemplate messageTemplate = MessageTemplate.MatchOntology("GetProductionDuration");

	/**
	 * @var EquipletAgent equipletAgent
	 *      The equipletAgent related to this behaviour.
	 */
	private EquipletAgent equipletAgent;

	/**
	 * Instantiates a new gets the production duration.
	 * 
	 * @param a The agent for this behaviour
	 * 
	 */
	public GetProductionDuration(Agent a) {
		super(a, messageTemplate);
		equipletAgent = (EquipletAgent) a;
	}

	/**
	 * Function to handle the incoming messages for this behaviour.
	 * Handles the response to the GetProductStepDuration question and asks the service agent the same question.
	 * 
	 * @param message - The received message.
	 */
	@Override
	public void handle(ACLMessage message) {
		Logger.logAclMessage(message, 'r');
		Logger.log(LogLevel.DEBUG, "%s received message from %s%n", myAgent.getLocalName(), message.getSender().getLocalName(),
				message.getOntology());
		try {
			// gets the productstepId and sends it to the service agent with the ontology GetProductionStepDuration.
			ObjectId productStepId = equipletAgent.getRelatedObjectId(message.getConversationId());
			if(productStepId == null) {
				Logger.log(LogLevel.DEBUG, "Conversation id not known");
				ACLMessage responseMessage = message.createReply();
				responseMessage.setPerformative(ACLMessage.DISCONFIRM);
				responseMessage.setOntology("ConversationIdUnknown");
				responseMessage.addUserDefinedParameter("message-id", new UID().toString());
				Logger.logAclMessage(responseMessage, 's');
				myAgent.send(responseMessage);
			} else {
				ProductionDurationResponse productionDurationResponseBehaviour =
						new ProductionDurationResponse(myAgent, equipletAgent.getProductStepBBClient());
				myAgent.addBehaviour(productionDurationResponseBehaviour);

				ACLMessage responseMessage = new ACLMessage(ACLMessage.REQUEST);
				responseMessage.addReceiver(equipletAgent.getServiceAgent());
				responseMessage.setConversationId(message.getConversationId());
				responseMessage.setContentObject(productStepId);
				responseMessage.setOntology("GetProductionStepDuration");
				responseMessage.addUserDefinedParameter("message-id", new UID().toString());
				Logger.logAclMessage(responseMessage, 's');
				myAgent.send(responseMessage);
			}
		} catch(IOException e) {
			Logger.log(LogLevel.ERROR, e);
			myAgent.doDelete();
		}
	}
}
