package ro.sapientia2015.story.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.junit.Test;

public class StatusTest {

	private String TITLE = "title";
    private String STATUS = "status";
    private String DESCRIPTION = "description";
    
    @Test
    public void buildWithMandatoryInformation() {
        Status built = Status.getBuilder(TITLE).build();
        assertNull(built.getId());
        assertEquals(TITLE, built.getTitle());
    }

    @Test
    public void buildWithAllInformation() {
        Status built = Status.getBuilder(TITLE)
                .description(DESCRIPTION)
                .build();

        assertNull(built.getId());
        assertEquals(DESCRIPTION, built.getDescription());
        assertEquals(TITLE, built.getTitle());
    }
    
}
