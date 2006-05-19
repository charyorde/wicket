/*
 * $Id$
 * $Revision$
 * $Date$
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.spring.direct.web;

import wicket.Application;
import wicket.model.IModel;
import wicket.spring.common.Contact;
import wicket.spring.common.ContactDao;
import wicket.spring.common.web.ContactDataProvider;
import wicket.spring.common.web.ExampleApplication;

public class DirectDataProvider extends ContactDataProvider {

	protected ContactDao getContactDao() {
		return ((ExampleApplication)Application.get()).getContactDao();
	}

	public IModel model(Object object) {
		return new DirectModel((Contact)object);
	}

}
