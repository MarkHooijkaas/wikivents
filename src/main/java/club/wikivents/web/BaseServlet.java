package club.wikivents.web;

import org.kisst.props4j.Props;
import org.kisst.servlet4j.AbstractServlet;

public abstract class BaseServlet extends AbstractServlet {
	public BaseServlet(Props props) {
		super(props);
	}
}
