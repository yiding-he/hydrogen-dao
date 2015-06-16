package other;

import com.hyd.dao.log.Logger;

/**
 * (description)
 * created at 2015/6/11
 *
 * @author Yiding
 */
public class TestLogger {

    public static void main(String[] args) {
        Logger.setLoggerFactory(Logger.LOGBACK_FACTORY);
        Logger logger = Logger.getLogger("test");
        logger.info("你好");
    }
}
