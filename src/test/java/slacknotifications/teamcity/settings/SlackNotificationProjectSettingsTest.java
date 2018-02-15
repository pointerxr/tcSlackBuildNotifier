package slacknotifications.teamcity.settings;


import jetbrains.buildServer.serverSide.settings.ProjectSettingsManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class SlackNotificationProjectSettingsTest {
	ProjectSettingsManager psm = mock(ProjectSettingsManager.class);
	
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void TestFactory(){
		SlackNotificationProjectSettingsFactory psf = new SlackNotificationProjectSettingsFactory(psm);
		psf.createProjectSettings("project1");
	}

	@Test
	public void TestSettings(){

	}
	
}
