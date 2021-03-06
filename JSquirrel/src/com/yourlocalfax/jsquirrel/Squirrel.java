/*
The MIT License (MIT)

Copyright (c) 2014 Christopher Foster

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package com.yourlocalfax.jsquirrel;

/**
 * The core Squirrel wrapper class.
 * Most methods in here will directly represent a native Squirrel function call in the squirrel.h file.
 * 
 * 
 * @author Christopher Foster
 */
public class Squirrel {
	
	static {
		System.loadLibrary("JSquirrel");
	}

	private static final int SQ_VMSTATE_IDLE =			0;
	private static final int SQ_VMSTATE_RUNNING =		1;
	private static final int SQ_VMSTATE_SUSPENDED =		2;

	public static final int SQOBJECT_REF_COUNTED =		0x08000000;
	public static final int SQOBJECT_NUMERIC =			0x04000000;
	public static final int SQOBJECT_DELEGABLE =		0x02000000;
	public static final int SQOBJECT_CANBEFALSE =		0x01000000;
	
	public static final int RT_MASK =					0x00FFFFFF;

	public static final int RT_NULL =					0x00000001;
	public static final int RT_INTEGER =				0x00000002;
	public static final int RT_FLOAT =					0x00000004;
	public static final int RT_BOOL =					0x00000008;
	public static final int RT_STRING =					0x00000010;
	public static final int RT_TABLE =					0x00000020;
	public static final int RT_ARRAY =					0x00000040;
	public static final int RT_USERDATA =				0x00000080;
	public static final int RT_CLOSURE =				0x00000100;
	public static final int RT_NATIVECLOSURE =			0x00000200;
	public static final int RT_GENERATOR =				0x00000400;
	public static final int RT_USERPOINTER =			0x00000800;
	public static final int RT_THREAD =					0x00001000;
	public static final int RT_FUNCPROTO =				0x00002000;
	public static final int RT_CLASS =					0x00004000;
	public static final int RT_INSTANCE =				0x00008000;
	public static final int RT_WEAKREF =				0x00010000;
	public static final int RT_OUTER =					0x00020000;
	
	public static boolean SQ_OK(JSqResult res) {
		return res.m_value == 0;
	}
	
	public static boolean SQ_ERROR(JSqResult res) {
		return res.m_value == -1;
	}
	
	public static boolean SQ_FAILED(JSqResult res) {
		return res.m_value < 0;
	}
	
	public static boolean SQ_SUCCEDED(JSqResult res) {
		return res.m_value >= 0;
	}
	
	// TODO SquirrelVM
	
	/**
	 * Creates a new instance of a Squirrel VM with a new execution stack.
	 * @param initialStackSize THe size of the stack in slots for the VM.
	 * @return The new Squirrel VM.
	 * @throws JSquirrelException
	 */
	public static JSqVM sq_open(int initialStackSize) throws JSquirrelException {
		long handle = sq_open_native(initialStackSize);
		if (handle == 0)
			throw new JSquirrelException("Could not create a new Squirrel VM.");
		return new JSqVM(handle);
	}
	
	private static native long sq_open_native(int initialStackSize);
	
	/**
	 * Creates a new VM friend of the one passed and pushes it to the stack as a "thread" object.
	 * @param friend The friend VM the new thread belongs to.
	 * @param initialStackSize The size of the stack in slots for the new thread.
	 * @return The new Squirrel VM.
	 * @throws JSquirrelException
	 */
	public static JSqVM sq_newthread(JSqVM friend, int initialStackSize) throws JSquirrelException {
		long handle = sq_newthread_native(friend.m_nativeHandle, initialStackSize);
		if (handle == 0)
			throw new JSquirrelException("Could not create a new thread.");
		return new JSqVM(handle);
	}
	
	private static native long sq_newthread_native(long v, int initialStackSize);
	
	/**
	 * Releases the Squirrel VM and all related friend VMs.
	 * @param v The Squirrel VM to close.
	 */
	public static void sq_close(JSqVM v) {
		sq_close_native(v.m_nativeHandle);
	}
	
	private static native void sq_close_native(long v);
	
	/**
	 * Suspends the execution of the target Squirrel VM.
	 * @param v The target Squirrel VM.
	 * @return The result of this operation.
	 * @see #SQ_SUCCEDED(JSqResult)
	 * @see #SQ_FAILED(JSqResult)
	 */
	public static JSqResult sq_suspendvm(JSqVM v) {
		return new JSqResult(sq_suspendvm_native(v.m_nativeHandle));
	}
	
	private static native int sq_suspendvm_native(long v);
	
	/**
	 * Wake up the execution of the previously suspended Squirrel VM.
	 * <br><br>
	 * If {@code resumedRet} is true a value will be popped from the stack and used as a return value for the function previously suspended in the VM.
	 * <br><br>
	 * If {@code retVal} is true the function will push the return value of the function that suspended this VMs execution or the main function one.
	 * <br><br>
	 * If raiseError is true a runtime error will invoke the error handler, if a runtime error occurs.
	 * <br><br>
	 * If throwError is true the VM will throw an exception as soon as it is resumed.
	 * The exception payload must be set beforehand by invoking {@link #sq_throwerror(JSqVM, String)}. 
	 * @param v The target Squirrel VM.
	 * @param resumedRet 
	 * @param retVal
	 * @param raiseError
	 * @param throwError
	 * @return The result of this operation.
	 * @see #SQ_SUCCEDED(JSqResult)
	 * @see #SQ_FAILED(JSqResult)
	 */
	public static JSqResult sq_wakeupvm(JSqVM v, boolean resumedRet, boolean retVal, boolean raiseError, boolean throwError) {
		return new JSqResult(sq_wakeupvm_native(v.m_nativeHandle, resumedRet, retVal, raiseError, throwError));
	}
	
	private static native int sq_wakeupvm_native(long v, boolean resumedRet, boolean retVal, boolean raiseError, boolean throwError);
	
	/**
	 * @param v The Squirrel VM in question.
	 * @return The execution state of the given Squirrel VM.
	 */
	public static JSqVMState sq_getvmstate(JSqVM v) {
		int res = sq_getvmstate_native(v.m_nativeHandle);
		switch (res) {
			case SQ_VMSTATE_IDLE:
				return JSqVMState.Idle;
			case SQ_VMSTATE_SUSPENDED:
				return JSqVMState.Suspended;
			case SQ_VMSTATE_RUNNING:
			default:
				return JSqVMState.Running;
		}
	}
	
	private static native int sq_getvmstate_native(long v);
	
	/**
	 * @return The version number of the Squirrel VM.
	 */
	public static int sq_getversion() {
		return sq_getversion_native();
	}
	
	public static native int sq_getversion_native();
	
	// TODO Compiler
	
	/**
	 * Compiles a Squirrel program from a String.
	 * If it succeedes, the compiled script is pushed as a function in the stack.
	 * <br><br>
	 * If raiseError is true the compiler error handler will be called in case of an error.
	 * @param v The target Squirrel VM.
	 * @param source The source code of the Squirrel program.
	 * @param sourceName The name of the Squirrel program.
	 * @param raiseError 
	 * @return The result of this operation.
	 * @see #SQ_SUCCEDED(JSqResult)
	 * @see #SQ_FAILED(JSqResult)
	 */
	public static JSqResult sq_compilebuffer(JSqVM v, String source, String sourceName, boolean raiseError) {
		return new JSqResult(sq_compilebuffer_native(v.m_nativeHandle, source, sourceName, raiseError));
	}
	
	private static native int sq_compilebuffer_native(long v, String source, String sourceName, boolean raiseError);
	
	public static void sq_enabledebuginfo(JSqVM v, boolean enable) {
		sq_enabledebuginfo_native(v.m_nativeHandle, enable);
	}
	
	private static native void sq_enabledebuginfo_native(long v, boolean enable);
	
	/**
	 * Enable/disable the error callback notification of handled exceptions.
	 * @param v The target Squirrel VM.
	 * @param enable
	 */
	public static void sq_notifyallexceptions(JSqVM v, boolean enable) {
		sq_notifyallexceptions_native(v.m_nativeHandle, enable);
	}
	
	private static native void sq_notifyallexceptions_native(long v, boolean enable);
	
	// TODO Stack Operations
	
	/**
	 * Pushes the value at {@code idx} in the stack.
	 * @param v The target Squirrel VM.
	 * @param idx
	 */
	public static void sq_push(JSqVM v, int idx) {
		sq_push_native(v.m_nativeHandle, idx);
	}
	
	private static native void sq_push_native(long v, int idx);
	
	/**
	 * Pops n elements form the stack.
	 * @param v The target Squirrel VM.
	 * @param numElementsToPop n elements to pop.
	 */
	public static void sq_pop(JSqVM v, int numElementsToPop) {
		sq_pop_native(v.m_nativeHandle, numElementsToPop);
	}
	
	private static native void sq_pop_native(long v, int numElementsToPop);
	
	/**
	 * Pops one object from the top of the stack.
	 * @param v The target Squirrel VM.
	 */
	public static void sq_poptop(JSqVM v) {
		sq_poptop_native(v.m_nativeHandle);
	}
	
	private static native void sq_poptop_native(long v);
	
	/**
	 * Removes the element at {@code idx} in the stack.
	 * @param v The target Squirrel VM.
	 * @param idx
	 */
	public static void sq_remove(JSqVM v, int idx) {
		sq_remove_native(v.m_nativeHandle, idx);
	}
	
	private static native void sq_remove_native(long v, int idx);
	
	/**
	 * @param v The target Squirrel VM.
	 * @return The index of the top of the stack.
	 */
	public static int sq_gettop(JSqVM v) {
		return sq_gettop_native(v.m_nativeHandle);
	}
	
	private static native int sq_gettop_native(long v);
	
	/**
	 * Resize the stack.
	 * If the new top is bigger than the current top the function will push nulls.
	 * @param v The target Squirrel VM.
	 * @param newtop The new top index.
	 */
	public static void sq_settop(JSqVM v, int newtop) {
		sq_settop_native(v.m_nativeHandle, newtop);
	}
	
	private static native void sq_settop_native(long v, int newtop);
	
	/**
	 * Ensure that the stack space left is at least of specified size.
	 * If the stack is smaller it will automatically grow.
	 * If there's a metamethod currently running the function will fail and the stack will not be resized, this situation has to be considered a "stack overflow".
	 * @param v The target Squirrel VM.
	 * @param nSize The new required stack size.
	 * @return The result of this operation.
	 * @see #SQ_SUCCEDED(JSqResult)
	 * @see #SQ_FAILED(JSqResult)
	 */
	public static JSqResult sq_reservestack(JSqVM v, int nSize) {
		return new JSqResult(sq_reservestack_native(v.m_nativeHandle, nSize));
	}
	
	private static native int sq_reservestack_native(long v, int nSize);

	/**
	 * Compares two objects from the stack.
	 * If the first object is greater than the second, returns a number greater than 0 (usually 1).
	 * If the two objects are equal, returns 0.
	 * Otherwise (the first object is less than the second) returns a number less than 0 (usually -1).
	 * @param v The target Squirrel VM.
	 * @return The result of the comparing.
	 */
	public static int sq_cmp(JSqVM v) {
		return sq_cmp_native(v.m_nativeHandle);
	}
	
	private static native int sq_cmp_native(long v);
	
	/**
	 * Pushes the object at the position {@code idx} in the source VM stack to the destination VM stack.
	 * @param dest The destination Squirrel VM.
	 * @param src The source Squirrel VM.
	 * @param idx The index in the source stack of the value that should be moved.
	 */
	public static void sq_move(JSqVM dest, JSqVM src, int idx) {
		sq_move_native(dest.m_nativeHandle, src.m_nativeHandle, idx);
	}
	
	private static native void sq_move_native(long v1, long v2, int idx);
	
	// TODO Object Creation Handling
	
	public static JSqUserPointer sq_newuserdata(JSqVM v, long size) {
		return new JSqUserPointer(sq_newuserdata_native(v.m_nativeHandle, size));
	}
	
	private static native long sq_newuserdata_native(long v, long size);
	
	public static void sq_newtable(JSqVM v) {
		sq_newtable_native(v.m_nativeHandle);
	}
	
	private static native void sq_newtable_native(long v);
	
	public static void sq_newtableex(JSqVM v, int initialCapacity) {
		sq_newtableex_native(v.m_nativeHandle, initialCapacity);
	}
	
	private static native void sq_newtableex_native(long v, int initialCapacity);
	
	public static void sq_newarray(JSqVM v, int size) {
		sq_newarray_native(v.m_nativeHandle, size);
	}
	
	private static native void sq_newarray_native(long v, int size);
	
	public static JSqResult sq_setparamscheck(JSqVM v, int nParamsCheck, String typeMask) {
		return new JSqResult(sq_setparamscheck_native(v.m_nativeHandle, nParamsCheck, typeMask));
	}
	
	private static native int sq_setparamscheck_native(long v, int nParamsCheck, String typeMask); 
	
	/**
	 * Pops an object from the stack (must be a table, instance or class), clones the closure at position {@code idx}
	 * in the stack and sets the popped object as the environment of the cloned closure.
	 * Then pushes the new cloned closure on the top of the stack.
	 * @param v The target Squirrel VM.
	 * @param idx
	 * @return The result of this operation.
	 * @see #SQ_SUCCEDED(JSqResult)
	 * @see #SQ_FAILED(JSqResult)
	 */
	public static JSqResult sq_bindenv(JSqVM v, int idx) {
		return new JSqResult(sq_bindenv_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_bindenv_native(long v, int idx);
	
	public static void sq_pushstring(JSqVM v, String s) {
		sq_pushstring_native(v.m_nativeHandle, s, s.length());
	}
	
	private static native void sq_pushstring_native(long v, String s, int len);
	
	public static void sq_pushfloat(JSqVM v, float f) {
		sq_pushfloat_native(v.m_nativeHandle, f);
	}
	
	private static native void sq_pushfloat_native(long v, float f);
	
	public static void sq_pushinteger(JSqVM v, int i) {
		sq_pushinteger_native(v.m_nativeHandle, i);
	}
	
	private static native void sq_pushinteger_native(long v, int i);
	
	public static void sq_pushbool(JSqVM v, boolean b) {
		sq_pushbool_native(v.m_nativeHandle, b);
	}
	
	private static native void sq_pushbool_native(long v, boolean b);
	
	public static void sq_pushserpointer(JSqVM v, JSqUserPointer up) {
		sq_pushserpointer_native(v.m_nativeHandle, up.m_nativeHandle);
	}
	
	private static native void sq_pushserpointer_native(long v, long up);
	
	public static void sq_pushnull(JSqVM v) {
		sq_pushnull_native(v.m_nativeHandle);
	}
	
	private static native void sq_pushnull_native(long v);
	
	public static JSqObjectType sq_gettype(JSqVM v, int idx) {
		int res = sq_gettype_native(v.m_nativeHandle, idx);
		return JSqObjectType.getFromValue(res);
	}
	
	private static native int sq_gettype_native(long v, int idx); // Returns int because SQObjectType stores ints
	
	public static JSqResult sq_typeof(JSqVM v, int idx) {
		return new JSqResult(sq_typeof_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_typeof_native(long v, int idx);
	
	public static int sq_getsize(JSqVM v, int idx) {
		return sq_getsize_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_getsize_native(long v, int idx);
	
	public static long sq_gethash(JSqVM v, int idx) {
		return sq_gethash_native(v.m_nativeHandle, idx);
	}
	
	private static native long sq_gethash_native(long v, int idx);
	
	public static JSqResult sq_getbase(JSqVM v, int idx) {
		return new JSqResult(sq_getbase_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_getbase_native(long v, int idx);
	
	public static boolean sq_instanceof(JSqVM v) {
		return sq_instanceof_native(v.m_nativeHandle);
	}
	
	private static native boolean sq_instanceof_native(long v);
	
	public static JSqResult sq_tostring(JSqVM v, int idx) {
		int res = sq_tostring_native(v.m_nativeHandle, idx);
		return new JSqResult(res);
	}
	
	private static native int sq_tostring_native(long v, int idx);
	
	public static boolean sq_tobool(JSqVM v, int idx) {
		return sq_tobool_native(v.m_nativeHandle, idx);
	}
	
	private static native boolean sq_tobool_native(long v, int idx);
	
	public static String sq_getstring(JSqVM v, int idx) {
		return sq_getstring_native(v.m_nativeHandle, idx);
	}
	
	private static native String sq_getstring_native(long v, int idx);
	
	public static int sq_getinteger(JSqVM v, int idx) {
		return sq_getinteger_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_getinteger_native(long v, int idx);
	
	public static float sq_getfloat(JSqVM v, int idx) {
		return sq_getfloat_native(v.m_nativeHandle, idx);
	}
	
	private static native float sq_getfloat_native(long v, int idx);
	
	/**
	 * Gets the value of the bool at position {@code idx} in the stack.
	 * @param v The target Squirrel VM.
	 * @param idx
	 * @return The retrieved bool.
	 */
	public static boolean sq_getbool(JSqVM v, int idx) {
		return sq_getbool_native(v.m_nativeHandle, idx);
	}
	
	private static native boolean sq_getbool_native(long v, int idx);
	
	public static JSqVM sq_getthread(JSqVM v, int idx) {
		return new JSqVM(sq_getthread_native(v.m_nativeHandle, idx));
	}
	
	private static native long sq_getthread_native(long v, int idx);
	
	public static JSqUserPointer sq_getuserpointer(JSqVM v, int idx) {
		return new JSqUserPointer(sq_getuserpointer_native(v.m_nativeHandle, idx));
	}
	
	private static native long sq_getuserpointer_native(long v, int idx);
	
	public static JSqUserData sq_getuserdata(JSqVM v, int idx) {
		long[] res = sq_getuserdata_native(v.m_nativeHandle, idx);
		return new JSqUserData(new JSqUserPointer(res[0]), new JSqUserPointer(res[1]));
	}
	
	private static native long[] sq_getuserdata_native(long v, int idx);
	
	public static JSqResult sq_settypetag(JSqVM v, int idx) {
		return new JSqResult(sq_settypetag_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_settypetag_native(long v, int idx);
	
	public static JSqUserPointer sq_gettypetag(JSqVM v, int idx) {
		return new JSqUserPointer(sq_gettypetag_native(v.m_nativeHandle, idx));
	}
	
	private static native long sq_gettypetag_native(long v, int idx);
	
	// releasehook, scratchpad
	
	// TODO DOES NOT WORK!
	public static JSqFunctionInfo sq_getfunctioninfo(JSqVM v, int idx) {
		JSqFunctionInfo res = new JSqFunctionInfo();
		long upid = sq_getfunctioninfo_native(v.m_nativeHandle, idx, res);
		res.m_funcid = new JSqUserPointer(upid);
		return res;
	}
	
	private static native long sq_getfunctioninfo_native(long v, int idx, JSqFunctionInfo info);

	// TODO PROBABLY DOES NOT WORK!
	public static JSqClosureInfo sq_getclosureinfo(JSqVM v, int idx) {
		int[] res = sq_getclosureinfo_native(v.m_nativeHandle, idx);
		return new JSqClosureInfo(res[0], res[1]);
	}
	
	private static native int[] sq_getclosureinfo_native(long v, int idx);

	// TODO PROBABLY DOES NOT WORK!
	public static JSqResult sq_getclosurename(JSqVM v, int idx) {
		return new JSqResult(sq_getclosurename_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_getclosurename_native(long v, int idx);

	// TODO PROBABLY DOES NOT WORK!
	public static JSqResult sq_setnativeclosurename(JSqVM v, int idx, String name) {
		return new JSqResult(sq_setnativeclosurename_native(v.m_nativeHandle, idx, name));
	}
	
	private static native int sq_setnativeclosurename_native(long v, int idx, String name);
	
	public static JSqResult sq_setinstanceup(JSqVM v, int idx, JSqUserPointer up) {
		return new JSqResult(sq_setinstanceup_native(v.m_nativeHandle, idx, up.m_nativeHandle));
	}
	
	private static native int sq_setinstanceup_native(long v, int idx, long up);
	
	public static JSqUserPointer sq_getinstanceup(JSqVM v, int idx, JSqUserPointer typetag) {
		return new JSqUserPointer(sq_getinstanceup_native(v.m_nativeHandle, idx, typetag.m_nativeHandle));
	}
	
	private static native long sq_getinstanceup_native(long v, int idx, long typetag);
	
	public static JSqResult sq_setclassudsize(JSqVM v, int idx, int udSize) {
		return new JSqResult(sq_setclassudsize_native(v.m_nativeHandle, idx, udSize));
	}
	
	private static native int sq_setclassudsize_native(long vmHaldne, int idx, int udSize);
	
	public static JSqResult sq_newclass(JSqVM v, boolean hasBase) {
		return new JSqResult(sq_newclass_native(v.m_nativeHandle, hasBase));
	}
	
	private static native int sq_newclass_native(long v, boolean hasBase);
	
	/**
	 * Creates an instance of the class at position {@code idx} in the stack.
	 * The new class instance is pushed on top of the stack.
	 * @param v The target Squirrel VM.
	 * @param idx
	 * @return The result of this operation.
	 * @see #SQ_SUCCEDED(JSqResult)
	 * @see #SQ_FAILED(JSqResult)
	 */
	public static JSqResult sq_createinstance(JSqVM v, int idx) {
		return new JSqResult(sq_createinstance_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_createinstance_native(long v, int idx);
	
	public static JSqResult sq_setattributes(JSqVM v, int idx) {
		return new JSqResult(sq_setattributes_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_setattributes_native(long v, int idx);
	
	public static JSqResult sq_getattributes(JSqVM v, int idx) {
		return new JSqResult(sq_getattributes_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_getattributes_native(long v, int idx);
	
	public static JSqResult sq_getclass(JSqVM v, int idx) {
		return new JSqResult(sq_getclass_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_getclass_native(long v, int idx);
	
	public static void sq_weakref(JSqVM v, int idx) {
		sq_weakref_native(v.m_nativeHandle, idx);
	}
	
	private static native void sq_weakref_native(long v, int idx);
	
	public static JSqResult sq_getdefaultdelegate(JSqVM v, JSqObjectType t) {
		return new JSqResult(sq_getdefaultdelegate_native(v.m_nativeHandle, t.tag));
	}
	
	private static native int sq_getdefaultdelegate_native(long v, int tag);
	
	public static JSqMemberHandle sq_getmemberhandle(JSqVM v, int idx) {
		return new JSqMemberHandle(sq_getmemberhandle_native(v.m_nativeHandle, idx));
	}
	
	private static native long sq_getmemberhandle_native(long v, int idx);
	
	/**
	 * Pushes the value of a class or instance member using a member handle.
	 * @param v
	 * @param idx
	 * @param memberHandle
	 * @return The result of this operation.
	 * @see #SQ_SUCCEDED(JSqResult)
	 * @see #SQ_FAILED(JSqResult)
	 * @see #sq_getmemberhandle(JSqVM, int)
	 */
	public static JSqResult sq_getbyhandle(JSqVM v, int idx, JSqMemberHandle memberHandle) {
		return new JSqResult(sq_getbyhandle_native(v.m_nativeHandle, idx, memberHandle.m_nativeHandle));
	}
	
	private static native int sq_getbyhandle_native(long v, int idx, long member);
	
	public static JSqResult sq_setbyhandle(JSqVM v, int idx, JSqMemberHandle memberHandle) {
		return new JSqResult(sq_setbyhandle_native(v.m_nativeHandle, idx, memberHandle.m_nativeHandle));
	}
	
	private static native int sq_setbyhandle_native(long v, int idx, long member);
	
	// TODO Object Manipulation

	/**
	 * Pushes the current root table in the stack.
	 * @param v The target Squirrel VM.
	 */
	public static void sq_pushroottable(JSqVM v) {
		sq_pushroottable_native(v.m_nativeHandle);
	}
	
	private static native void sq_pushroottable_native(long v);

	/**
	 * Pushes the current registry table in the stack.
	 * @param v The target Squirrel VM.
	 */
	public static void sq_pushregistrytable(JSqVM v) {
		sq_pushregistrytable_native(v.m_nativeHandle);
	}
	
	private static native void sq_pushregistrytable_native(long v);
	
	/**
	 * Pushes the current const table in the stack.
	 * @param v The target Squirrel VM.
	 */
	public static void sq_pushconsttable(JSqVM v) {
		sq_pushconsttable_native(v.m_nativeHandle);
	}
	
	private static native void sq_pushconsttable_native(long v);
	
	/**
	 * Pops a table from the stack and sets it as the root table.
	 * @param v The target Squirrel VM.
	 * @return The result of this operation.
	 * @see #SQ_SUCCEDED(JSqResult)
	 * @see #SQ_FAILED(JSqResult)
	 */
	public static JSqResult sq_setroottable(JSqVM v) {
		return new JSqResult(sq_setroottable_native(v.m_nativeHandle));
	}
	
	private static native int sq_setroottable_native(long v);
	
	/**
	 * Pops a table from the stack and sets it as a const table.
	 * @param v The target Squirrel VM.
	 * @return The result of this operation.
	 * @see #SQ_SUCCEDED(JSqResult)
	 * @see #SQ_FAILED(JSqResult)
	 */
	public static JSqResult sq_setconsttable(JSqVM v) {
		return new JSqResult(sq_setconsttable_native(v.m_nativeHandle));
	}
	
	private static native int sq_setconsttable_native(long v);
	
	public static JSqResult sq_newslot(JSqVM v, int idx, boolean bStatic) {
		return new JSqResult(sq_newslot_native(v.m_nativeHandle, idx, bStatic));
	}
	
	private static native int sq_newslot_native(long v, int idx, boolean bStatic);
	
	public static JSqResult sq_deleteslot(JSqVM v, int idx, boolean pushVal) {
		return new JSqResult(sq_deleteslot_native(v.m_nativeHandle, idx, pushVal));
	}
	
	private static native int sq_deleteslot_native(long v, int idx, boolean pushVal);
	
	public static JSqResult sq_set(JSqVM v, int idx) {
		return new JSqResult(sq_set_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_set_native(long v, int idx);
	
	public static JSqResult sq_get(JSqVM v, int idx) {
		return new JSqResult(sq_get_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_get_native(long v, int idx);
	
	public static JSqResult sq_rawset(JSqVM v, int idx) {
		return new JSqResult(sq_rawset_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_rawset_native(long v, int idx);
	
	public static JSqResult sq_rawget(JSqVM v, int idx) {
		return new JSqResult(sq_rawget_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_rawget_native(long v, int idx);
	
	public static JSqResult sq_rawdeleteslot(JSqVM v, int idx, boolean pushVal) {
		return new JSqResult(sq_rawdeleteslot_native(v.m_nativeHandle, idx, pushVal));
	}
	
	private static native int sq_rawdeleteslot_native(long v, int idx, boolean pushVal);
	
	public static JSqResult sq_newmenber(JSqVM v, int idx, boolean bStatic) {
		return new JSqResult(sq_newmember_native(v.m_nativeHandle, idx, bStatic));
	}
	
	private static native int sq_newmember_native(long v, int idx, boolean bStatic);
	
	public static JSqResult sq_rawnewmenber(JSqVM v, int idx, boolean bStatic) {
		return new JSqResult(sq_rawnewmember_native(v.m_nativeHandle, idx, bStatic));
	}
	
	private static native int sq_rawnewmember_native(long v, int idx, boolean bStatic);
	
	public static JSqResult sq_arrayappend(JSqVM v, int idx) {
		return new JSqResult(sq_arrayappend_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_arrayappend_native(long v, int idx);
	
	public static JSqResult sq_arraypop(JSqVM v, int idx, boolean pushVal) {
		return new JSqResult(sq_arraypop_native(v.m_nativeHandle, idx, pushVal));
	}
	
	private static native int sq_arraypop_native(long v, int idx, boolean pushVal);
	
	public static JSqResult sq_arrayresize(JSqVM v, int idx, int newSize) {
		return new JSqResult(sq_arrayresize_native(v.m_nativeHandle, idx, newSize));
	}
	
	private static native int sq_arrayresize_native(long v, int idx, int newSize);
	
	public static JSqResult sq_arrayreverse(JSqVM v, int idx) {
		return new JSqResult(sq_arrayreverse_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_arrayreverse_native(long v, int idx);
	
	public static JSqResult sq_arrayremove(JSqVM v, int idx, int itemIdx) {
		return new JSqResult(sq_arrayremove_native(v.m_nativeHandle, idx, itemIdx));
	}
	
	private static native int sq_arrayremove_native(long v, int idx, int itemIdx);
	
	public static JSqResult sq_arrayinsert(JSqVM v, int idx, int destPos) {
		return new JSqResult(sq_arrayinsert_native(v.m_nativeHandle, idx, destPos));
	}
	
	private static native int sq_arrayinsert_native(long v, int idx, int destPos);
	
	public static JSqResult sq_setdelegate(JSqVM v, int idx) {
		return new JSqResult(sq_setdelegate_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_setdelegate_native(long v, int idx);
	
	public static JSqResult sq_getdelegate(JSqVM v, int idx) {
		return new JSqResult(sq_getdelegate_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_getdelegate_native(long v, int idx);
	
	public static JSqResult sq_clone(JSqVM v, int idx) {
		return new JSqResult(sq_clone_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_clone_native(long v, int idx);
	
	public static JSqResult sq_setfreevariable(JSqVM v, int idx, long nVal) {
		return new JSqResult(sq_setfreevariable_native(v.m_nativeHandle, idx, nVal));
	}
	
	private static native int sq_setfreevariable_native(long v, int idx, long nVal);
	
	public static JSqResult sq_next(JSqVM v, int idx) {
		return new JSqResult(sq_next_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_next_native(long v, int idx);
	
	public static JSqResult sq_getweakrefval(JSqVM v, int idx) {
		return new JSqResult(sq_getweakrefval_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_getweakrefval_native(long v, int idx);
	
	public static JSqResult sq_clear(JSqVM v, int idx) {
		return new JSqResult(sq_clear_native(v.m_nativeHandle, idx));
	}
	
	private static native int sq_clear_native(long v, int idx);
	
	// TODO Calls
	
	public static JSqResult sq_call(JSqVM v, int numParams, boolean retval, boolean raiseError) {
		return new JSqResult(sq_call_native(v.m_nativeHandle, numParams, retval, raiseError));
	}
	
	private static native int sq_call_native(long v, int numParams, boolean retval, boolean raiseError);
	
	public static JSqResult sq_resume(JSqVM v, boolean retval, boolean raiseError) {
		return new JSqResult(sq_resume_native(v.m_nativeHandle, retval, raiseError));
	}
	
	private static native int sq_resume_native(long v, boolean retval, boolean raiseError);
	
	public static String sq_getlocal(JSqVM v, long level, long idx) {
		return sq_getlocal_native(v.m_nativeHandle, level, idx);
	}
	
	private static native String sq_getlocal_native(long v, long level, long idx);
	
	public static JSqResult sq_getcallee(JSqVM v) {
		return new JSqResult(sq_getcallee_native(v.m_nativeHandle));
	}
	
	private static native int sq_getcallee_native(long v);
	
	public static String sq_getfreevariable(JSqVM v, int idx, long nVal) {
		return sq_getfreevariable_native(v.m_nativeHandle, idx, nVal);
	}
	
	private static native String sq_getfreevariable_native(long v, int idx, long nVal);
	
	public static JSqResult sq_throwerror(JSqVM v, String err) {
		return new JSqResult(sq_throwerror_native(v.m_nativeHandle, err));
	}
	
	private static native int sq_throwerror_native(long v, String err);
	
	public static JSqResult sq_throwobject(JSqVM v) {
		return new JSqResult(sq_throwobject_native(v.m_nativeHandle));
	}
	
	private static native int sq_throwobject_native(long v);
	
	public static void sq_reseterror(JSqVM v) {
		sq_reseterror_native(v.m_nativeHandle);
	}
	
	private static native void sq_reseterror_native(long v);
	
	public static void sq_getlasterror(JSqVM v) {
		sq_getlasterror_native(v.m_nativeHandle);
	}
	
	private static native void sq_getlasterror_native(long v);
	
	// Raw Object Handling
	
	public static JSqObject sq_getstackobj(JSqVM v, int idx) throws JSquirrelException {
		long handle = sq_getstackobj_native(v.m_nativeHandle, idx);
		if (handle == 0)
			throw new JSquirrelException("Could not get stack object.");
		return new JSqObject(handle);
	}
	
	private static native long sq_getstackobj_native(long v, int idx);
	
	public static void sq_pushobject(JSqVM v, JSqObject obj) {
		sq_pushobject_native(v.m_nativeHandle, obj.m_nativeHandle);
	}
	
	private static native void sq_pushobject_native(long v, long obj);
	
	// sq_addref
	
	public static boolean sq_release(JSqVM v, JSqObject obj) {
		return sq_release_native(v.m_nativeHandle, obj.m_nativeHandle);
	}
	
	private static native boolean sq_release_native(long v, long obj);
	
	public static int sq_getrefcount(JSqVM v, JSqObject obj) {
		return (int) sq_getrefcount_native(v.m_nativeHandle, obj.m_nativeHandle);
	}
	
	private static native long sq_getrefcount_native(long v, long obj);
	
	public static void sq_resetobject(JSqObject obj) {
		sq_resetobject_native(obj.m_nativeHandle);
	}
	
	private static native void sq_resetobject_native(long obj);
	
	public static String sq_objtostring(JSqObject obj) {
		return sq_objtostring_native(obj.m_nativeHandle);
	}
	
	private static native String sq_objtostring_native(long obj);
	
	public static boolean sq_objtobool(JSqObject obj) {
		return sq_objtobool_native(obj.m_nativeHandle);
	}
	
	private static native boolean sq_objtobool_native(long obj);
	
	public static int sq_objtointeger(JSqObject obj) {
		return sq_objtointeger_native(obj.m_nativeHandle);
	}
	
	private static native int sq_objtointeger_native(long obj);
	
	public static float sq_objtofloat(JSqObject obj) {
		return sq_objtofloat_native(obj.m_nativeHandle);
	}
	
	private static native float sq_objtofloat_native(long obj);
	
	public static JSqUserPointer sq_objtouserpointer(JSqObject obj) {
		return new JSqUserPointer(sq_objtouserpointer_native(obj.m_nativeHandle));
	}
	
	private static native long sq_objtouserpointer_native(long obj);
	
	public static JSqUserPointer sq_getobjtypetag(JSqObject obj) {
		return new JSqUserPointer(sq_getobjtypetag_native(obj.m_nativeHandle));
	}
	
	private static native long sq_getobjtypetag_native(long obj);
	
	// GC
	
	public static int sq_collectgarbage(JSqVM v) {
		return sq_collectgarbage_native(v.m_nativeHandle);
	}
	
	private static native int sq_collectgarbage_native(long v);
	
	public static JSqResult sq_resurrectunreachable(JSqVM v) {
		return new JSqResult(sq_resurrectunreachable_native(v.m_nativeHandle));
	}
	
	private static native int sq_resurrectunreachable_native(long v);
	
	// Serialization
	
	// Memory Allocation
	
	public static native long sq_malloc(long size); // the long return is a pointer to the void *
	
	public static native void sq_realloc(long p, long oldSize, long newSize);
	
	public static native void sq_free(long p, long size);
	
	// Utility Macro
	
	public static boolean sq_isnumeric(JSqObject o) {
		return sq_isnumeric_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isnumeric_native(long o);
	
	public static boolean sq_istable(JSqObject o) {
		return sq_istable_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_istable_native(long o);
	
	public static boolean sq_isarray(JSqObject o) {
		return sq_isarray_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isarray_native(long o);
	
	public static boolean sq_isfunction(JSqObject o) {
		return sq_isfunction_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isfunction_native(long o);
	
	public static boolean sq_isclosure(JSqObject o) {
		return sq_isclosure_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isclosure_native(long o);
	
	public static boolean sq_isgenerator(JSqObject o) {
		return sq_isgenerator_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isgenerator_native(long o);
	
	public static boolean sq_isnativeclosure(JSqObject o) {
		return sq_isnativeclosure_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isnativeclosure_native(long o);
	
	public static boolean sq_isstring(JSqObject o) {
		return sq_isstring_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isstring_native(long o);
	
	public static boolean sq_isinteger(JSqObject o) {
		return sq_isinteger_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isinteger_native(long o);
	
	public static boolean sq_isfloat(JSqObject o) {
		return sq_isfloat_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isfloat_native(long o);
	
	public static boolean sq_isuserpointer(JSqObject o) {
		return sq_isuserpointer_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isuserpointer_native(long o);
	
	public static boolean sq_isuserdata(JSqObject o) {
		return sq_isnumeric_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isuserdata_native(long o);
	
	public static boolean sq_isthread(JSqObject o) {
		return sq_isthread_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isthread_native(long o);
	
	public static boolean sq_isnull(JSqObject o) {
		return sq_isnull_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isnull_native(long o);
	
	public static boolean sq_isclass(JSqObject o) {
		return sq_isclass_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isclass_native(long o);
	
	public static boolean sq_isinstance(JSqObject o) {
		return sq_isinstance_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isinstance_native(long o);
	
	public static boolean sq_isbool(JSqObject o) {
		return sq_isbool_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isbool_native(long o);
	
	public static boolean sq_issweakref(JSqObject o) {
		return sq_isweakref_native(o.m_nativeHandle);
	}
	
	private static native boolean sq_isweakref_native(long o);
	
	public static JSqObjectType sq_type(JSqObject o) {
		return JSqObjectType.getFromValue(sq_type_native(o.m_nativeHandle));
	}
	
	private static native int sq_type_native(long o);
	
}
