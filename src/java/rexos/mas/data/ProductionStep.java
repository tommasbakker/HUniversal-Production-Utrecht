/**
 * @file rexos/mas/data/ProductionStep.java
 * @brief Class in which information about the current production step can be
 *        obtained
 * @date Created: 02-04-2013
 * 
 * @author Mike Schaap
 * 
 * @section LICENSE License: newBSD
 * 
 *          Copyright � 2012, HU University of Applied Sciences Utrecht. All
 *          rights reserved.
 * 
 *          Redistribution and use in source and binary forms, with or without
 *          modification, are permitted provided that the following conditions
 *          are met: - Redistributions of source code must retain the above
 *          copyright notice, this list of conditions and the following
 *          disclaimer. - Redistributions in binary form must reproduce the
 *          above copyright notice, this list of conditions and the following
 *          disclaimer in the documentation and/or other materials provided with
 *          the distribution. - Neither the name of the HU University of Applied
 *          Sciences Utrecht nor the names of its contributors may be used to
 *          endorse or promote products derived from this software without
 *          specific prior written permission.
 * 
 *          THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *          "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *          LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 *          FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE HU
 *          UNIVERSITY OF APPLIED SCIENCES UTRECHT BE LIABLE FOR ANY DIRECT,
 *          INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *          (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *          SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *          HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 *          STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 *          ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 *          OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 **/

package rexos.mas.data;

import jade.core.AID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.annotations.SerializedName;
import com.mongodb.BasicDBObject;

public class ProductionStep implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -832835122145455883L;
	private int _requiredTimeSlots;
	
	private int _id;
	
	private int _capability;
	
	private StepStatusCode _status = StepStatusCode.EVALUATING;
	
	private BasicDBObject _parameters;
	
	private String _convId;
	
	//List of equiplets capable of executing this productStep
	private ArrayList<AID> _capableEquiplets;
	
	private HashMap<AID, String> _equipletConversationIds;
	
	private HashMap<AID, Long> _equipletDuration;
	
	private AID _usedEquiplet;

	public ProductionStep() {
		this._parameters = new BasicDBObject();
		_equipletConversationIds = new HashMap<AID, String>();
	}
	
	public ProductionStep(int id, int capability, BasicDBObject parameterList) {
		this._id = id;
		this._capability = capability;
		this._parameters = parameterList;
		_equipletConversationIds = new HashMap<AID, String>();
	}


	public int getId(){
		return this._id;
	}

	public StepStatusCode getStatus(){
		return this._status;
	}

	public void setStatus(StepStatusCode status){
		this._status = status;
	}

	public void setRequiredTimeSlots(int timeSlots){
		this._requiredTimeSlots = timeSlots;
	}

	public int getRequiredTimeSlots(){
		return this._requiredTimeSlots;
	}
	
	public BasicDBObject getParameters() {
		return _parameters;
	}

	public int getCapability() {
		return _capability;
	}
	
	public String getConversationId() {
		return this._convId;
	}
	
	public void setConversationId(String value) {
		this._convId = value;
	}
	
	public void setConversationIdForEquiplet(AID equipletId, String converdationid) {
		this._equipletConversationIds.put(equipletId, converdationid);
	}
	
	public String getConversationIdForEquiplet(AID equipletId) {
		return this._equipletConversationIds.get(equipletId);
	}
	
	public void setDurationForEquiplet(AID equipletId, long timeslots) {
		this._equipletDuration.put(equipletId, timeslots);
	}
	
	public long getDurationForEquiplet(AID equipletId) {
		return this._equipletDuration.get(equipletId);
	}
	
	public AID getUsedEquiplet() {
		return _usedEquiplet;
	}
	
	public void setUsedEquiplet(AID value) {
		_usedEquiplet = value;
	}
	
	@Override
	public String toString() {
	   return "DataObject [id=" + _id + ", capability=" + _capability + ", parameters="
		+ _parameters + "]";
	}
}
