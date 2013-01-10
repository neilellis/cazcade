/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.server.rest.test.jbehave;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.junit.JUnitStory;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.CandidateSteps;
import org.jbehave.core.steps.InstanceStepsFactory;

import java.util.List;

/**
 * Top level class used to provide configuration for all the fountain stories.
 */
public abstract class FountainStory extends JUnitStory {
    @Override
    public List<CandidateSteps> candidateSteps() {
        //For now only work with the FountainSteps class...
        return new InstanceStepsFactory(configuration(), new FountainSteps()).createCandidateSteps();
    }

    @Override
    public Configuration configuration() {
        //Simple default configuration for now.
        return new MostUsefulConfiguration().useStoryLoader(new LoadFromClasspath(getClass().getClassLoader()))
                                            .useStoryReporterBuilder(new StoryReporterBuilder().withDefaultFormats()
                                                                                               .withFormats(StoryReporterBuilder.Format.CONSOLE, StoryReporterBuilder.Format.TXT));
    }
}
