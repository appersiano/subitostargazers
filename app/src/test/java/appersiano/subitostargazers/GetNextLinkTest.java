package appersiano.subitostargazers;

import android.support.v4.util.Pair;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import appersiano.subitostargazers.getstargazers.GetStargazersPresenter;


public class GetNextLinkTest {

    private HashMap<String, String> linkHash;

    @Before
    public void setUp() {
        linkHash = new HashMap<>();
        linkHash.put(GetStargazersPresenter.NEXT, "/repo/test/ok?page=2");
    }

    @Test
    public void testGetNextLink_isValid() {
        Pair<String,String> testMe = GetStargazersPresenter.getNextPathAndPagePair(linkHash);
        Assert.assertNotNull(testMe);
    }
}