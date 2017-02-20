package appersiano.subitostargazers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import appersiano.subitostargazers.getstargazers.GetStargazersPresenter;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DataForGithubQueryValidTest {

    private String owner;

    /* Array of tests */
    @Parameterized.Parameters
    public static Collection<String> data() {
        return new ArrayList<>(
        Arrays.asList("code-crusher","appersiano","CamelCaseUser", "ok_user")
        );
    }

    /* Constructor */
    public DataForGithubQueryValidTest(String ownerName) {
        this.owner = ownerName;
    }

    @Before
    public void setUp() {

    }

    @Test
    public void testOwnerName_isValid() {
        assertTrue(GetStargazersPresenter.ownerIsValid(owner));
    }
}