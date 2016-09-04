package club.wikivents.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.eclipse.jetty.server.Request;
import org.kisst.http4j.ActionHandler;
import org.kisst.http4j.HttpCall;
import org.kisst.http4j.ResourceHandler;
import org.kisst.http4j.form.HttpFormData;
import org.kisst.http4j.handlebar.TemplateEngine.TemplateData;
import org.kisst.item4j.struct.HashStruct;
import org.kisst.item4j.struct.MultiStruct;
import org.kisst.item4j.struct.Struct;
import org.kisst.util.CallInfo;
import org.kisst.util.PasswordEncryption;
import org.kisst.util.StringUtil;

import club.wikivents.model.User;
import club.wikivents.model.UserCommands.RemoveRecommendationCommand;
import club.wikivents.model.UserItem;
import club.wikivents.model.WikiventsModel;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

public class TagHandler extends ActionHandler<WikiventsCall, String> {

	private final WikiventsModel model;

	public TagHandler(WikiventsSite site) { super(WikiventsCall.class, String.class);
		this.model=site.model;
	}

	public void listAll(WikiventsCall call) {
		call.output(call.getTheme().userList, model.users);
	}

	@NeedsNoAuthorization
	public void view(WikiventsCall call, String tag) {
		TemplateData data=new TemplateData(call);
		data.add("tag", tag);
		data.add("users",model.userTags.records(tag));
		data.add("events",model.eventTags.records(tag));
		call.output(call.getTheme().tagShow, data);
	}


	@Override protected String findRecord(WikiventsCall call, String id) { return id; }

	@Override protected void checkChangeAccess(WikiventsCall call, String methodName, String oldRecord) {}

	@Override protected void checkViewAccess(WikiventsCall call, String methodName, String record) {}

	@Override public void handle(HttpCall httpcall, String subPath) {
		WikiventsCall call=WikiventsCall.of(httpcall, model);
		handleCall(call, subPath);
	}
}

