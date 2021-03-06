/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.ajax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.util.lang.Args;

/**
 * Attributes of an Ajax Request.
 * 
 * <hr>
 * 
 * <p>
 * Note that some of these attributes represent javascript functions. Those functions get a
 * <code>RequestQueueItem</code> instance as first argument. The instance provides access to
 * following properties that the javascript functions can use:
 * <dl>
 * <dt>attributes</dt>
 * <dd>Object with request queue item attributes. The <code>attributes</code> Object contains
 * following properties:
 * <dl>
 * 
 * <dt>component</dt>
 * <dd>component DOM element or <code>null</code> if the behavior is attached to page</dd>
 * 
 * <dt>formId</dt>
 * <dd>id of the form DOM element or <code>null</code> if not specified</dd>
 * 
 * <dt>token</dt>
 * <dd>token string or <code>null</code> if not specified</dd>
 * 
 * <dt></dd>
 * 
 * </dl>
 * </dd>
 * 
 * <dt>event</dt>
 * <dd>If the AJAX request was a result of javacript event (i.e. onclick) the <code>event</code>
 * property contains the actual event instance.
 * 
 * </dl>
 * 
 * @author Matej Knopp
 */
public final class AjaxRequestAttributes
{

	/**
	 * The method to be used when submitting a form
	 */
	public static enum Method {
		/** get */
		GET,

		/** post */
		POST;

		@Override
		public String toString()
		{
			return name();
		}
	}

	public static final String XML_DATA_TYPE = "xml";

	private boolean multipart = false;
	private Method method = Method.GET;
	private Integer requestTimeout;
	private boolean allowDefault = false;

	/**
	 * The name of the event that will trigger the Ajax call
	 */
	private String eventName = null;

	/**
	 * The id of the for that should be submitted
	 */
	private String formId = null;

	/**
	 * The id of the button/link that submitted the form
	 */
	private String submittingComponentName = null;

	/**
	 * Indicates whether or not this AjaxBehavior will produce <ajax-response>. By default it will
	 * produce it but some behaviors may need to return their own response which shouldn't be
	 * processed by wicket-ajax.js
	 */
	private boolean wicketAjaxResponse = true;

	private String dataType = XML_DATA_TYPE;

	private List<CharSequence> preconditions = null;
	private List<CharSequence> beforeHandlers = null;
	private List<CharSequence> afterHandlers = null;
	private List<CharSequence> successHandlers = null;
	private List<CharSequence> errorHandlers = null;
	private Map<String, Object> extraParameters = null;
	private List<CharSequence> dynamicExtraParameters = null;
	private List<CharSequence> requestQueueItemCreationListeners = null;
	private List<ExpressionDecorator> expressionDecorators = null;
	private AjaxChannel channel = null;

	/**
	 * Whether or not to use asynchronous XMLHttpRequest
	 */
	private boolean async = true;

	/**
	 * Returns whether the form submit is multipart.
	 * <p>
	 * Note that for multipart AJAX requests a hidden IFRAME will be used and that can have negative
	 * impact on error detection.
	 * 
	 * @return <code>true</code> if the form submit should be multipart, <code>false</code>
	 *         otherwise
	 */
	public boolean isMultipart()
	{
		return multipart;
	}

	/**
	 * Determines whether the form submit is multipart.
	 * 
	 * <p>
	 * Note that for multipart AJAX requests a hidden IFRAME will be used and that can have negative
	 * impact on error detection.
	 * 
	 * @param multipart
	 * @return this object
	 */
	public AjaxRequestAttributes setMultipart(boolean multipart)
	{
		this.multipart = multipart;
		return this;
	}

	/**
	 * Returns whether the Ajax request should be a <code>POST</code> regardless of whether a form
	 * is being submitted.
	 * <p>
	 * For a <code>POST</code>request all URL arguments are submitted as body. This can be useful if
	 * the URL parameters are longer than maximal URL length.
	 * 
	 * @return <code>true</code> if the request should be post, <code>false</code> otherwise.
	 */
	public Method getMethod()
	{
		return method;
	}

	/**
	 * Determines whether the Ajax request should be a <code>POST</code> regardless of whether a
	 * form is being submitted.
	 * <p>
	 * For a <code>POST</code>request all URL arguments are submitted as body. This can be useful if
	 * the URL parameters are longer than maximal URL length.
	 * 
	 * @param method
	 * @return this object
	 */
	public AjaxRequestAttributes setMethod(Method method)
	{
		this.method = Args.notNull(method, "method");
		return this;
	}

	/**
	 * Returns the timeout in milliseconds for the AJAX request. This only involves the actual
	 * communication and not the processing afterwards. Can be <code>null</code> in which case the
	 * default request timeout will be used.
	 * 
	 * @return request timeout in milliseconds or <code>null<code> for default timeout
	 */
	public Integer getRequestTimeout()
	{
		return requestTimeout;
	}

	/**
	 * Sets the timeout in milliseconds for the AJAX request. This only involves the actual
	 * communication and not the processing afterwards. Can be <code>null</code> in which case the
	 * default request timeout will be used.
	 * 
	 * @param requestTimeout
	 * @return this object
	 */
	public AjaxRequestAttributes setRequestTimeout(Integer requestTimeout)
	{
		this.requestTimeout = requestTimeout;
		return this;
	}

	/**
	 * Array of javascript functions that are invoked before the request executes. The functions
	 * will get a <code>RequestQueueItem</code> instance passed as fist argument and have to return
	 * a boolean value. If any of these functions returns <code>false</code> the request is
	 * canceled.
	 * <p>
	 * Example of single function:
	 * 
	 * <pre>
	 *    function(requestQueueItem) 
	 *    { 
	 *      if (someCondition()) 
	 *      {
	 *         return true; 
	 *      } 
	 *      else 
	 *      {
	 *         return false;
	 *      }
	 *    }
	 * </pre>
	 * 
	 * Preconditions can also be asynchronous (with the rest of the queue waiting until precondition
	 * finishes). An example of asynchronous precondition:
	 * 
	 * <pre>
	 *    function(requestQueueItem, makeAsync, asyncReturn) 
	 *    { 
	 *      makeAsync(); // let the queue know that this precondition is asynchronous
	 *      var f = function()
	 *      {
	 *        if (someCondition())
	 *        {
	 *          asyncReturn(true); // return the precondition value
	 *        }
	 *        else
	 *        {
	 *          asyncReturn(false); // return the precondition value
	 *        }
	 *      };
	 *      window.setTimeout(f, 1000); // postpone the actual check 1000 millisecond. The queue will wait.
	 *    }
	 * </pre>
	 * 
	 * @return List<CharSequence>
	 */
	public List<CharSequence> getPreconditions()
	{
		if (preconditions == null)
		{
			preconditions = new ArrayList<CharSequence>();
		}
		return preconditions;
	}

	/**
	 * Array of javascript functions that are invoked before the actual AJAX request. This
	 * invocation only happens when all precondition functions return true. Each of the function
	 * will get a <code>RequestQueueItem</code> instance passed as first argument.
	 * <p>
	 * Example of single function:
	 * 
	 * <pre>
	 *    function(requestQueueItem) 
	 *    { 
	 *      doSomethingWhenAjaxRequestBegins();
	 *    }
	 * </pre>
	 * 
	 * @see #getPreconditions()
	 * 
	 * @return List<CharSequence>
	 */
	public List<CharSequence> getBeforeHandlers()
	{
		if (beforeHandlers == null)
		{
			beforeHandlers = new ArrayList<CharSequence>();
		}
		return beforeHandlers;
	}

	/**
	 * Array of JavaScript functions that are invoked after the AJAX request if fired. This
	 * invocation only happens when all precondition functions return true.
	 * <p>
	 * Example of single function:
	 * 
	 * <pre>
	 * function()
	 * {
	 * 	doSomethingWhenAjaxRequestIsFired();
	 * }
	 * </pre>
	 * 
	 * @see #getPreconditions()
	 * 
	 * @return List<CharSequence>
	 */
	public List<CharSequence> getAfterHandlers()
	{
		if (afterHandlers == null)
		{
			afterHandlers = new ArrayList<CharSequence>();
		}
		return afterHandlers;
	}

	/**
	 * Array of javascript functions that are invoked after the request is successfully processed.
	 * Each function will get a <code>RequestQueueItem</code> instance passed as fist argument.
	 * <p>
	 * Example of single function:
	 * 
	 * <pre>
	 *    function(requestQueueItem) 
	 *    { 
	 *      doSomethingWhenRequestIsSuccessfullyProcessed();
	 *    }
	 * </pre>
	 * 
	 * @return List<CharSequence>
	 */
	public List<CharSequence> getSuccessHandlers()
	{
		if (successHandlers == null)
		{
			successHandlers = new ArrayList<CharSequence>();
		}
		return successHandlers;
	}

	/**
	 * Array of javascript functions that are invoked when an unexpected error happens during the
	 * AJAX request or the processing afterwards, or when some of the timeouts is exceeded. The
	 * functions will get a <code>RequestQueueItem</code> passed as fist argument. If possible error
	 * message will be second argument passed to the handlers.
	 * <p>
	 * Example of single function:
	 * 
	 * <pre>
	 *    function(requestQueueItem, error) 
	 *    { 
	 *      if (typeof(error) == &quot;string&quot;) 
	 *      {
	 *      	alert('Error processing request ' + error);
	 *      }
	 *    }
	 * </pre>
	 * 
	 * @return List<CharSequence>
	 */
	public List<CharSequence> getErrorHandlers()
	{
		if (errorHandlers == null)
		{
			errorHandlers = new ArrayList<CharSequence>();
		}
		return errorHandlers;
	}

	/**
	 * Map that contains additional (static) URL parameters. These will be appended to the request
	 * URL. This is simpler alternative to {@link #getDynamicExtraParameters()}
	 * 
	 * @return Map with additional URL arguments
	 */
	public Map<String, Object> getExtraParameters()
	{
		if (extraParameters == null)
		{
			extraParameters = new HashMap<String, Object>();
		}
		return extraParameters;
	}

	/**
	 * Array of JavaScript functions that produce additional URL arguments. Each of the functions
	 * must return a <code>Map&lt;String, String&gt;</code> (Object).
	 * <p>
	 * Example of single function:
	 * 
	 * <pre>
	 *    function()
	 *    {
	 *    	return { 'param1': document.body.tagName }
	 *    }
	 * </pre>
	 * 
	 * @return a list of functions that produce additional URL arguments.
	 */
	public List<CharSequence> getDynamicExtraParameters()
	{
		if (dynamicExtraParameters == null)
		{
			dynamicExtraParameters = new ArrayList<CharSequence>();
		}
		return dynamicExtraParameters;
	}

	/**
	 * Array of javascript functions invoked when a <code>RequestQueueItem</code> instance is
	 * created. The <code>RequestQueueItem</code> instance will be passed as first argument.
	 * 
	 * @return List<CharSequence> or <code>null</code>
	 */
	public List<CharSequence> getRequestQueueItemCreationListeners()
	{
		if (requestQueueItemCreationListeners == null)
		{
			requestQueueItemCreationListeners = new ArrayList<CharSequence>();
		}
		return requestQueueItemCreationListeners;
	}

	/**
	 * Certain behaviors support decorating the javascript expression they generate with
	 * {@link ExpressionDecorator}s.
	 * <p>
	 * This usually decorates the javascript expression that is responsible for creating
	 * <code>RequestQueueItem</code> and adding it to the queue. The decorator may be useful to
	 * intercept the <code>RequestQueueItem</code> creation on in the earliest possible time.
	 * <p>
	 * Note that not all Ajax behaviors are required to support decorating the expression.
	 * 
	 * @return list of {@link ExpressionDecorator} .
	 */
	public List<ExpressionDecorator> getExpressionDecorators()
	{
		if (expressionDecorators == null)
		{
			expressionDecorators = new ArrayList<ExpressionDecorator>();
		}
		return expressionDecorators;
	}

	/**
	 * Only applies for event behaviors. Returns whether the behavior should allow the default event
	 * handler to be invoked. For example if the behavior is attached to a link and
	 * {@link #isAllowDefault()} returns <code>false</code> (which is default value), the link's URL
	 * will not be followed. If {@link #isAllowDefault()} returns <code>true</code>, the link URL
	 * will be loaded (and the onclick handler fired if there is any).
	 * 
	 * @return <code>true</code> if the default event handler should be invoked, <code>false</code>
	 *         otherwise.
	 */
	public boolean isAllowDefault()
	{
		return allowDefault;
	}

	/**
	 * Only applies for event behaviors. Determines whether the behavior should allow the default
	 * event handler to be invoked.
	 * 
	 * @see #isAllowDefault()
	 * 
	 * @param allowDefault
	 * @return this object
	 */
	public AjaxRequestAttributes setAllowDefault(boolean allowDefault)
	{
		this.allowDefault = allowDefault;
		return this;
	}

	/**
	 * @param async
	 *            a flag whether to do asynchronous Ajax call or not
	 * @return this object
	 */
	public AjaxRequestAttributes setAsynchronous(final boolean async)
	{
		this.async = async;
		return this;
	}

	/**
	 * @return whether to do asynchronous Ajax call
	 */
	public boolean isAsynchronous()
	{
		return async;
	}

	/**
	 * @return the channel to use
	 */
	public AjaxChannel getChannel()
	{
		return channel;
	}

	/**
	 * @param channel
	 * @return this object
	 */
	public AjaxRequestAttributes setChannel(AjaxChannel channel)
	{
		this.channel = channel;
		return this;
	}

	/**
	 * @return the name of the event that will trigger the Ajax call
	 */
	public String getEventName()
	{
		return eventName;
	}

	/**
	 * @param eventName
	 *            the name of the event that will trigger the Ajax call
	 * @return this object
	 */
	public AjaxRequestAttributes setEventName(String eventName)
	{
		this.eventName = eventName;
		return this;
	}

	/**
	 * @return the id of the for that should be submitted
	 */
	public String getFormId()
	{
		return formId;
	}

	/**
	 * @param formId
	 *            the id of the for that should be submitted
	 * @return this object
	 */
	public AjaxRequestAttributes setFormId(String formId)
	{
		this.formId = formId;
		return this;
	}

	/**
	 * @return the input name of the button/link that submitted the form
	 */
	public String getSubmittingComponentName()
	{
		return submittingComponentName;
	}

	/**
	 * @param submittingComponentName
	 *            the input name of the button/link that submitted the form
	 * @return this object
	 */
	public AjaxRequestAttributes setSubmittingComponentName(String submittingComponentName)
	{
		this.submittingComponentName = submittingComponentName;
		return this;
	}

	/**
	 * @return
	 */
	public Boolean isWicketAjaxResponse()
	{
		return wicketAjaxResponse;
	}

	/**
	 * @param wicketAjaxResponse
	 * @return this object
	 */
	public AjaxRequestAttributes setWicketAjaxResponse(Boolean wicketAjaxResponse)
	{
		this.wicketAjaxResponse = wicketAjaxResponse;
		return this;
	}

	/**
	 * @return
	 */
	public String getDataType()
	{
		return dataType;
	}

	/**
	 * @param dataType
	 * @return
	 */
	public AjaxRequestAttributes setDataType(String dataType)
	{
		this.dataType = dataType;
		return this;
	}

}