/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.container.jbossas.remote_5_0;

import java.util.Properties;

import javax.management.MBeanServerConnection;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;

import junit.framework.Assert;

import org.jboss.arquillian.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.spi.client.protocol.metadata.ProtocolMetaData;
import org.junit.Ignore;
import org.junit.Test;

/**
 * ProfileServiceTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@Ignore // not automated
public class ProfileServiceTestCase
{

   @Test
   public void shouldBeAbleToExtractData() throws Exception
   {
      InitialContext ctx = createContext();
      MBeanServerConnection serverConnection = getServerConnection(ctx);

      String deploymentName = "test.ear";

      ProtocolMetaData metaData = ManagementViewParser.parse(deploymentName, serverConnection);
      
      Assert.assertNotNull(metaData);
      
      HTTPContext context = metaData.getContext(HTTPContext.class);
      Assert.assertNotNull(context);
      
      Assert.assertEquals("127.0.0.1", context.getHost());
      Assert.assertEquals(8080, context.getPort());
      
      // jsp/default not included
      Assert.assertEquals(1, context.getServlets().size());
   }

   
   private InitialContext createContext() throws Exception
   {
         Properties props = new Properties();
         props.put(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
         props.put(InitialContext.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
         props.put(InitialContext.PROVIDER_URL, "jnp://localhost:1099");
         return new InitialContext(props);
   }
   
   public MBeanServerConnection getServerConnection(InitialContext context) throws Exception
   {
      String adapterName = "jmx/rmi/RMIAdaptor";
      Object obj = context.lookup(adapterName);
      if ( obj == null )
      {
         throw new NameNotFoundException("Object " + adapterName + " not found.");
      }
      return (MBeanServerConnection)obj;
   }

}
