/**
 * @file rexos/mas/data/StepStatusCode.java
 * @brief Provides an enum with status codes for a product/service/equiplet step.
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
package rexos.mas.data;

/**
 * Enum with status codes for product/service/equiplet steps.
 */
public enum StepStatusCode {
	/**
	 * Step is evaluating.
	 */
	EVALUATING(0),
	/**
	 * Step is planned.
	 */
	PLANNED(1),
	/**
	 * Step is waiting for production.
	 */
	WAITING(2),
	/**
	 * Step is in progress.
	 */
	IN_PROGRESS(3),
	/**
	 * Step is suspended (possibly because of a warning).
	 */
	SUSPENDED_OR_WARNING(4),
	/**
	 * Step is done.
	 */
	DONE(5),
	/**
	 * Step is aborted.
	 */
	ABORTED(6),
	/**
	 * Step has failed.
	 */
	FAILED(7),
	/**
	 * Step should be deleted.
	 */
	DELETED(8),
	
	RESCHEDULE(9);

	/**
	 * @var int status
	 * The status
	 */
	private int status;
	
	/**
	 * Constructor for a status.
	 * 
	 * @param status - The status
	 */
	private StepStatusCode(int status){
		this.status = status;
	}
	
	/**
	 * Function for getting the status.
	 * 
	 * @return The status as an int.
	 */
	public int getStatus(){
		return status;
	}
}
