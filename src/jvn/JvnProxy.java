package jvn;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import annotations.Read;
import annotations.Write;
import irc.Sentence;

public class JvnProxy implements InvocationHandler{
	private JvnObject object;
	
	private JvnProxy(String name, Object obj) throws JvnException{
		try {
			JvnServerImpl js = JvnServerImpl.jvnGetServer();
			object = js.jvnLookupObject(name);
			if (object == null) {
				object = js.jvnCreateObject((Serializable) new Sentence());
				object.jvnUnLock();
				js.jvnRegisterObject(name, object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object newInstance(String name, Class obj) throws Exception {
		return Proxy.newProxyInstance(obj.getClassLoader(), obj.getInterfaces(), new JvnProxy(name, obj));
	}

	public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
		Object result = null;
		try {
			if (m.isAnnotationPresent(Read.class)) object.jvnLockRead();
			else if (m.isAnnotationPresent(Write.class)) object.jvnLockWrite();
			else throw new Exception("Annotation error");
			result = m.invoke(object.jvnGetObjectState(), args);
			object.jvnUnLock();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
