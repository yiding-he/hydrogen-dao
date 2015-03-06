package other;

import com.hyd.dao.DAO;
import com.hyd.daotest.BaseTest;
import org.junit.Test;

/**
 * (description)
 * created at 2014/12/25
 *
 * @author Yiding
 */
public class TestStackTrace extends BaseTest {

    @Override
    public void setUp() {
        // do nothing
    }

    @Test
    public void testStackTrace() throws Exception {
        DAO dao = getDAO();
        dao.query("select * from users");
    }
}
