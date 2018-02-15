package slacknotifications.teamcity.settings;

import jetbrains.buildServer.serverSide.ServerPaths;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import slacknotifications.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SlackNotificationSettingsTest {

	@After
	@Before
	public void deleteSlackConfigFile(){
		DeleteConfigFiles();
	}
	
    private void DeleteConfigFiles() {
		File outputFile = new File("slack", "slack-config.xml");
		outputFile.delete();

		File outputDir = new File("slack");
		outputDir.delete();
	}
	
    @Ignore
	@Test
	public void test_AuthFailWrongCredsUsingProxyFromConfig() throws FileNotFoundException, IOException, InterruptedException {
		String expectedConfigDirectory = ".";
		ServerPaths serverPaths = mock(ServerPaths.class);
		when(serverPaths.getConfigDir()).thenReturn(expectedConfigDirectory);

		SlackNotificationTest test = new SlackNotificationTest();
		SlackNotificationMainConfig mainConfig = new SlackNotificationMainConfig(serverPaths);
		mainConfig.setProxyHost(test.proxy);
		mainConfig.setProxyPort(test.proxyPort);
		mainConfig.setProxyShortNames(true);
		String url = "http://" + test.webserverHost + ":" + test.webserverPort + "/200";
		SlackNotification w = new SlackNotificationImpl(url, mainConfig.getProxyConfig());
		// w.setProxyUserAndPass("somethingIncorrect", "somethingIncorrect");
		SlackNotificationTestServer s = test.startWebServer();
		SlackNotificationTestProxyServer p = test.startProxyServerAuth("somthingCorrect", "somethingCorrect");
		w.setEnabled(true);
		w.post();
		test.stopWebServer(s);
		test.stopProxyServer(p);
		assertTrue(w.getStatus() == HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
	}

    @Ignore
	@Test
	public void test_AuthFailNoCredsUsingProxyFromConfig() throws FileNotFoundException, IOException, InterruptedException {
		String expectedConfigDirectory = ".";
		ServerPaths serverPaths = mock(ServerPaths.class);
		when(serverPaths.getConfigDir()).thenReturn(expectedConfigDirectory);

		SlackNotificationTest test = new SlackNotificationTest();
		SlackNotificationMainConfig mainConfig = new SlackNotificationMainConfig(serverPaths);
		mainConfig.setProxyHost(test.proxy);
		mainConfig.setProxyPort(test.proxyPort);
		mainConfig.setProxyShortNames(true);
		String url = "http://" + test.webserverHost + ":" + test.webserverPort + "/200";
		SlackNotification w = new SlackNotificationImpl(url, mainConfig.getProxyConfig());
		// w.setProxyUserAndPass("somethingIncorrect", "somethingIncorrect");
		SlackNotificationTestServer s = test.startWebServer();
		SlackNotificationTestProxyServer p = test.startProxyServerAuth("somethingCorrect", "somethingCorrect");
		w.setEnabled(true);
		w.post();
		test.stopWebServer(s);
		test.stopProxyServer(p);
		assertTrue(w.getStatus() == HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
	}

    /*
    @Ignore
	@Test
	public void test_AuthPassNoCredsUsingProxyFromConfig() throws FileNotFoundException, IOException, InterruptedException {
		SlackNotificationTest test = new SlackNotificationTest();
		SlackNotificationMainConfig mainConfig = new SlackNotificationMainConfig();
		mainConfig.setProxyHost(test.proxy);
		mainConfig.setProxyPort(test.proxyPort);
		mainConfig.setProxyShortNames(true);
		String url = "http://" + test.webserverHost + ":" + test.webserverPort + "/200";
		SlackNotification w = new SlackNotificationImpl(url, mainConfig.getProxyConfigForUrl(url));
		SlackNotificationTestServer s = test.startWebServer();
		SlackNotificationTestProxyServer p = test.startProxyServer();
		w.setEnabled(true);
		w.post();
		test.stopWebServer(s);
		test.stopProxyServer(p);
		assertTrue(w.getStatus() == HttpStatus.SC_OK);
	}
	*/
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_WebookConfig() throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder();
		List<SlackNotificationConfig> configs = new ArrayList<SlackNotificationConfig>();
		builder.setIgnoringElementContentWhitespace(true);
			Document doc = builder.build("src/test/resources/testdoc2.xml");
			Element root = doc.getRootElement();
			if(root.getChild("slackNotifications") != null){
				Element child = root.getChild("slackNotifications");
				if ((child.getAttribute("enabled") != null) && (child.getAttribute("enabled").equals("true"))){
					List<Element> namedChildren = child.getChildren("slackNotification");
					for(Iterator<Element> i = namedChildren.iterator(); i.hasNext();)
		            {
						Element e = i.next();
						SlackNotificationConfig whConfig = new SlackNotificationConfig(e);
						configs.add(whConfig);
		            }
				}
			}

		
		for (SlackNotificationConfig c : configs){
			SlackNotification wh = new SlackNotificationImpl(c.getChannel());
			wh.setEnabled(c.getEnabled());
			//slackNotification.addParams(c.getParams());
			System.out.println(wh.getChannel());
			System.out.println(wh.isEnabled().toString());

		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void test_ReadXml() throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		//builder.setValidation(true);
		builder.setIgnoringElementContentWhitespace(true);
		
			Document doc = builder.build("src/test/resources/testdoc1.xml");
			Element root = doc.getRootElement();
			System.out.println(root.toString());
			if(root.getChild("slackNotifications") != null){
				Element child = root.getChild("slackNotifications");
				if ((child.getAttribute("enabled") != null) && (child.getAttribute("enabled").equals("true"))){
					List<Element> namedChildren = child.getChildren("slackNotification");
					for(Iterator<Element> i = namedChildren.iterator(); i.hasNext();)
		            {
						Element e = i.next();
						System.out.println(e.toString() + e.getAttributeValue("url"));
						//assertTrue(e.getAttributeValue("url").equals("http://something"));
						if(e.getChild("parameters") != null){
							Element eParams = e.getChild("parameters");
							List<Element> paramsList = eParams.getChildren("param");
							for(Iterator<Element> j = paramsList.iterator(); j.hasNext();)
							{
								Element eParam = j.next();
								System.out.println(eParam.toString() + eParam.getAttributeValue("name"));
								System.out.println(eParam.toString() + eParam.getAttributeValue("value"));
							}
						}
		            }
				}
			}

	}
}
