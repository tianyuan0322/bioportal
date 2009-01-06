package org.ncbo.stanford.util.helper.reflection;

/**
 * Class to hold arguments to be used for dynamic method invocation
 * 
 * @author Michael Dorf
 * 
 */
@SuppressWarnings("unchecked")
public class ArgumentHolder {
	protected Class[] cl;
	protected Object[] args;
	protected int argc;

	ArgumentHolder() {
		argc = 0;
		cl = new Class[0];
		args = new Object[0];
	}

	ArgumentHolder(int argc) {
		this.argc = argc;
		cl = new Class[argc];
		args = new Object[argc];
	}

	public Class[] getArgumentClasses() {
		return cl;
	}

	public Object[] getArguments() {
		return args;
	}

	public int setArgument(boolean b) {
		return this.setArgument(argc, new Boolean(b), Boolean.TYPE);
	}

	public int setArgument(int argnum, boolean b) {
		return this.setArgument(argnum, new Boolean(b), Boolean.TYPE);
	}

	public int setArgument(byte b) {
		return this.setArgument(argc, new Byte(b), Byte.TYPE);
	}

	public int setArgument(int argnum, byte b) {
		return this.setArgument(argnum, new Byte(b), Byte.TYPE);
	}

	public int setArgument(char c) {
		return this.setArgument(argc, new Character(c), Character.TYPE);
	}

	public int setArgument(int argnum, char c) {
		return this.setArgument(argnum, new Character(c), Character.TYPE);
	}

	public int setArgument(int i) {
		return this.setArgument(argc, new Integer(i), Integer.TYPE);
	}

	public int setArgument(int argnum, int i) {
		return this.setArgument(argnum, new Integer(i), Integer.TYPE);
	}

	public int setArgument(short s) {
		return this.setArgument(argc, new Short(s), Short.TYPE);
	}

	public int setArgument(int argnum, short s) {
		return this.setArgument(argnum, new Short(s), Short.TYPE);
	}

	public int setArgument(long l) {
		return this.setArgument(argc, new Long(l), Long.TYPE);
	}

	public int setArgument(int argnum, long l) {
		return this.setArgument(argnum, new Long(l), Long.TYPE);
	}

	public int setArgument(float f) {
		return this.setArgument(argc, new Float(f), Float.TYPE);
	}

	public int setArgument(int argnum, float f) {
		return this.setArgument(argnum, new Float(f), Float.TYPE);
	}

	public int setArgument(double d) {
		return this.setArgument(argc, new Double(d), Double.TYPE);
	}

	public int setArgument(int argnum, double d) {
		return this.setArgument(argnum, new Double(d), Double.TYPE);
	}

	public int setArgument(Object obj) {
		return this.setArgument(argc, obj, obj.getClass());
	}

	public int setArgument(int argnum, Object obj) {
		return this.setArgument(argnum, obj, obj.getClass());
	}

	public int setArgument(int argnum, Object obj, Class c) {
		if (argnum >= args.length) {
			argc = argnum + 1;
			Class[] clExpanded = new Class[argc];
			Object[] argsExpanded = new Object[argc];
			System.arraycopy(cl, 0, clExpanded, 0, cl.length);
			System.arraycopy(args, 0, argsExpanded, 0, args.length);
			cl = clExpanded;
			args = argsExpanded;
		}

		args[argnum] = obj;
		cl[argnum] = c;

		return argnum;
	}
}
