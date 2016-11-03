/**
 * Software License
 *
 * Copyright 2007/2010 National Library of New Zealand.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *
 * or the file "LICENSE.txt" included with the software.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package nz.govt.natlib.ndha.manualdeposit.provenanceevent;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import nz.govt.natlib.ndha.manualdeposit.metadata.IMetaDataTypeExtended;

import org.junit.Before;
import org.junit.Test;

public class ProvenanceEventDetailTest {
	private boolean eventOutcome;
	private String eventOutcomeDetail;

	private String eventType;
	private String eventIdentifierType;
	private String eventIdentifierValue;
	private ProvenanceEvent provenanceEvent;
	private String eventDescription; 

	@Before
	public void setup() {
		eventOutcome = true;
		eventOutcomeDetail = "eventOutcomeDetail";
		eventType = "eventType";
		eventIdentifierType = "eventIdentifierType";
		eventIdentifierValue = "eventIdentifierValue";
		eventDescription = "eventDescription";

		provenanceEvent = new ProvenanceEvent(eventIdentifierType,
				eventIdentifierValue, eventType, eventOutcome,
				eventOutcomeDetail, 100,eventDescription);
	}

	@Test
	public void testToStringContainsAllValues() {
		String toString = provenanceEvent.toString();

		assertThat(toString, containsString("SUCCESS"));
		assertThat(toString, containsString(eventOutcomeDetail));

		assertThat(toString, containsString(eventType));
		assertThat(toString, containsString(eventIdentifierType));
		assertThat(toString, containsString(eventIdentifierValue));
		assertThat(toString, containsString(eventDescription));
	}

	@Test
	public void testToMetadataTypeContainsAllValues() {
		IMetaDataTypeExtended dataTypeExtended = provenanceEvent
				.toMetadataType("test_note_1");

		assertThat(dataTypeExtended.getProvenanceEventOutcome(),
				is(equalTo(eventOutcome)));
		assertThat(dataTypeExtended.getProvenanceEventOutcomeDetail(),
				is(equalTo(eventOutcomeDetail)));

		assertThat(dataTypeExtended.getProvenanceNoteEventType(),
				is(equalTo(eventType)));
		assertThat(dataTypeExtended.getProvenanceEventIdentifierType(),
				is(equalTo(eventIdentifierType)));
		assertThat(dataTypeExtended.getProvenanceEventIdentifierValue(),
				is(equalTo(eventIdentifierValue)));
	}

}
