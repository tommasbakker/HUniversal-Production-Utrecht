package rexos.mas.hardware_agent.behaviours;

/**
 * @file CheckForModules.java
 * @brief Handles the CheckForModules message
 * @date Created: 12-04-13
 *
 * @author Thierry Gerritse
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

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import rexos.mas.behaviours.ReceiveBehaviour;
import rexos.mas.hardware_agent.HardwareAgent;

public class CheckForModules extends ReceiveBehaviour {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static MessageTemplate messageTemplate = MessageTemplate
			.MatchOntology("CheckForModules");
	private HardwareAgent hardwareAgent;

	/**
	 * 
	 * Instantiates a new check for module.
	 * 
	 */
	public CheckForModules(Agent a) {
		super(a, -1, messageTemplate);
		hardwareAgent = (HardwareAgent) a;
	}

	/**
	 * 
	 * @param ACLMessage
	 *            message handles a incoming messages and will check what kind a
	 *            module its need
	 */
	@Override
	public void handle(ACLMessage message) {
		int[] moduleIds = null;
		try {
			try {
				moduleIds = (int[]) message.getContentObject();

				ACLMessage reply;
				reply = message.createReply();
				reply.setOntology("CheckForModulesResponse");

				boolean modulesPresent = true;

				// KnowledgeDBClient client = KnowledgeDBClient.getClient();
				//
				// ResultSet resultSet;
				//
				// resultSet = client.executeSelectQuery(Queries.MODULES);
				// while (resultSet.next()) {
				// System.out.println(new Row(resultSet));
				// if (resultSet.equals(contentString)) {
				// modulesPresent = true;
				// }
				// }
				// System.out.println();

				for (int moduleId : moduleIds) {

					try {

						if (hardwareAgent.getLeadingModule(moduleId) == 0) {
							modulesPresent = false;
						}

					} catch (Exception e) {
						
						System.out.println("Exception Caught. No module found.");
						
					}
				}

				if (modulesPresent) {

					reply.setPerformative(ACLMessage.CONFIRM);

				} else {
					reply.setPerformative(ACLMessage.DISCONFIRM);
				}

				reply.setOntology("CheckForModulesResponse");
				myAgent.send(reply);

				/**
				 * checks in knowledge database if the requested modules // are
				 * available // if available set performative
				 * (ACLMessage.Confirm) else set // performative
				 * (ACLMessage.Disconfirm)
				 **/
			} catch (UnreadableException e) {
				// System.out.println("Exception Caught, No Content Object Given");
			}
			System.out.format("%s received message from %s (%s:%s)%n",
					myAgent.getLocalName(), message.getSender().getLocalName(),
					message.getOntology(), moduleIds);

		} catch (Exception e) {
			e.printStackTrace();
			// TODO: ERROR HANDLING
			myAgent.doDelete();
		}
	}
}