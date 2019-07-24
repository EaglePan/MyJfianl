package com.demo.blog;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.demo.common.model.Blog;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;

/**
 * 本 demo 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * 
 * 所有 sql 与业务逻辑写在 Service 中，不要放在 Model 中，更不
 * 要放在 Controller 中，养成好习惯，有利于大型项目的开发与维护
 */
@Before(BlogInterceptor.class)
public class BlogController extends Controller {
	
	@Inject
	BlogService service;
	
	public void index() {

		Page<Blog> page=service.paginate(getParaToInt(0, 1), 10);
		setAttr("blogPage", page);
		render("blog.html");
	}

	public void add() {
	}

	/**
	 * save 与 update 的业务逻辑在实际应用中也应该放在 serivce 之中，
	 * 并要对数据进正确性进行验证，在此仅为了偷懒
	 */
	/**
	 * Controller事务处理
	 * @throws Exception
	 */
	@Before({BlogValidator.class,Tx.class})
	public void save() throws Exception{
		Blog newB=getBean(Blog.class);

		if(newB.getStr("content").indexOf("ERROR")==-1){
			newB.save();
		}else{
			throw new Exception("异常！！！！！！！");
		}

		Blog b=new Blog();
		b.set("title","事务测试！");
		b.set("content","好好好好好");
		b.save();


		redirect("/blog");
	}

	/**
	 * service事务处理
	 * @throws Exception
	 */

	@Before({BlogValidator.class})
	public void saveOp() throws Exception{
		service.saveOp(getBean(Blog.class));
		redirect("/blog");
	}



	/**
	 * 多个service事务处理(controller加TX,service可不用加)
	 * @throws Exception
	 */
	@Before({BlogValidator.class,Tx.class})
	public void saveOp2() throws Exception{
		service.saveOp(getBean(Blog.class));
		service.saveOp2(getBean(Blog.class));
		redirect("/blog");
	}





	public void edit() {
		setAttr("blog", service.findById(getParaToInt()));
	}
	
	/**
	 * save 与 update 的业务逻辑在实际应用中也应该放在 serivce 之中，
	 * 并要对数据进正确性进行验证，在此仅为了偷懒
	 */
	@Before(BlogValidator.class)
	public void update() {
		getBean(Blog.class).update();
		redirect("/blog");
	}
	
	public void delete() {
		service.deleteById(getParaToInt());
		redirect("/blog");
	}
}


