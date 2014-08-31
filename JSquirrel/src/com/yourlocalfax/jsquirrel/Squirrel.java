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

public class Squirrel {
	
	static {
		System.loadLibrary("JSquirrel");
	}

	public static final int SQ_VMSTATE_IDLE =		0;
	public static final int SQ_VMSTATE_RUNNING =	1;
	public static final int SQ_VMSTATE_SUSPENDED =	2;

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
	
	// TODO Learn JNI stuff better and use everything, not just these!
	
	public static boolean SQ_OK(int res) {
		return res == 0;
	}
	
	public static boolean SQ_ERROR(int res) {
		return res == -1;
	}
	
	public static boolean SQ_FAILED(int res) {
		return res < 0;
	}
	
	public static boolean SQ_SUCCEDED(int res) {
		return res >= 0;
	}
	
	// TODO SquirrelVM
	
	public static JSqVM sq_open(int initialStackSize) throws JSquirrelException {
		long handle = sq_open_native(initialStackSize);
		if (handle == 0)
			throw new JSquirrelException("Could not create a new Squirrel VM.");
		return new JSqVM(handle);
	}
	
	private static native long sq_open_native(int initialStackSize);
	
	public static JSqVM sq_newthread(JSqVM friend, int initialStackSize) throws JSquirrelException {
		long handle = sq_newthread_native(friend.m_nativeHandle, initialStackSize);
		if (handle == 0)
			throw new JSquirrelException("Could not create a new thread.");
		return new JSqVM(handle);
	}
	
	private static native long sq_newthread_native(long v, int initialStackSize);
	
	public static void sq_close(JSqVM v) {
		sq_close_native(v.m_nativeHandle);
	}
	
	private static native void sq_close_native(long v);
	
	public static int sq_suspendvm(JSqVM v) {
		return sq_suspendvm_native(v.m_nativeHandle);
	}
	
	private static native int sq_suspendvm_native(long v);
	
	public static int sq_wakeupvm(JSqVM v, boolean resumedRet, boolean retVal, boolean raiseError, boolean throwError) {
		return sq_wakeupvm_native(v.m_nativeHandle, resumedRet, retVal, raiseError, throwError);
	}
	
	private static native int sq_wakeupvm_native(long v, boolean resumedRet, boolean retVal, boolean raiseError, boolean throwError);
	
	public static int sq_getvmstate(JSqVM v) {
		return sq_getvmstate_native(v.m_nativeHandle);
	}
	
	private static native int sq_getvmstate_native(long v);
	
	public static native int sq_getversion();
	
	// TODO Compiler
	
	public static int sq_compilebuffer(JSqVM v, String source, String sourceName, boolean raiseError) {
		return sq_compilebuffer_native(v.m_nativeHandle, source, sourceName, raiseError);
	}
	
	private static native int sq_compilebuffer_native(long v, String source, String sourceName, boolean raiseError);
	
	// TODO Stack Operations
	
	public static void sq_push(JSqVM v, int idx) {
		sq_push_native(v.m_nativeHandle, idx);
	}
	
	private static native void sq_push_native(long v, int idx);
	
	public static void sq_pop(JSqVM v, int numElementsToPop) {
		sq_pop_native(v.m_nativeHandle, numElementsToPop);
	}
	
	private static native void sq_pop_native(long v, int numElementsToPop);
	
	public static void sq_poptop(JSqVM v) {
		sq_poptop_native(v.m_nativeHandle);
	}
	
	private static native void sq_poptop_native(long v);
	
	public static void sq_remove(JSqVM v, int idx) {
		sq_remove_native(v.m_nativeHandle, idx);
	}
	
	private static native void sq_remove_native(long v, int idx);
	
	public static int sq_gettop(JSqVM v) {
		return sq_gettop_native(v.m_nativeHandle);
	}
	
	private static native int sq_gettop_native(long v);
	
	public static void sq_settop(JSqVM v, int newtop) {
		sq_settop_native(v.m_nativeHandle, newtop);
	}
	
	private static native void sq_settop_native(long v, int newtop);
	
	public static int sq_reservestack(JSqVM v, int nSize) {
		return sq_reservestack_native(v.m_nativeHandle, nSize);
	}
	
	private static native int sq_reservestack_native(long v, int nSize);
	
	public static void sq_move(JSqVM dest, JSqVM src, int idx) {
		sq_move_native(dest.m_nativeHandle, src.m_nativeHandle, idx);
	}
	
	private static native void sq_move_native(long v1, long v2, int idx);
	
	// TODO Object Creation Handling
	
	public static long sq_newuserdata(JSqVM v, long size) {
		return sq_newuserdata_native(v.m_nativeHandle, size);
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
	
	public static int sq_setparamscheck(JSqVM v, int nParamsCheck, String typeMask) {
		return sq_setparamscheck_native(v.m_nativeHandle, nParamsCheck, typeMask);
	}
	
	private static native int sq_setparamscheck_native(long v, int nParamsCheck, String typeMask); 
	
	public static int sq_bindenv(JSqVM v, int idx) {
		return sq_bindenv_native(v.m_nativeHandle, idx);
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
	
	public static void sq_pushserpointer(JSqVM v, long up) {
		sq_pushserpointer_native(v.m_nativeHandle, up);
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
	
	public static int sq_typeof(JSqVM v, int idx) {
		return sq_typeof_native(v.m_nativeHandle, idx);
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
	
	public static int sq_getbase(JSqVM v, int idx) {
		return sq_getbase_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_getbase_native(long v, int idx);
	
	public static boolean sq_instanceof(JSqVM v) {
		return sq_instanceof_native(v.m_nativeHandle);
	}
	
	private static native boolean sq_instanceof_native(long v);
	
	public static int sq_tostring(JSqVM v, int idx) {
		return sq_tostring_native(v.m_nativeHandle, idx);
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
	
	public static boolean sq_getbool(JSqVM v, int idx) {
		return sq_getbool_native(v.m_nativeHandle, idx);
	}
	
	private static native boolean sq_getbool_native(long v, int idx);
	
	public static JSqVM sq_getthread(JSqVM v, int idx) {
		return new JSqVM(sq_getthread_native(v.m_nativeHandle, idx));
	}
	
	private static native long sq_getthread_native(long v, int idx);
	
	public static long sq_getuserpointer(JSqVM v, int idx) {
		return sq_getuserpointer_native(v.m_nativeHandle, idx);
	}
	
	private static native long sq_getuserpointer_native(long v, int idx);
	
	// getuserdata
	
	public static void sq_settypetag(JSqVM v, int idx) {
		sq_settypetag_native(v.m_nativeHandle, idx);
	}
	
	private static native void sq_settypetag_native(long v, int idx);
	
	public static int sq_setinstanceup(JSqVM v, int idx, long up) {
		return sq_setinstanceup_native(v.m_nativeHandle, idx, up);
	}
	
	private static native int sq_setinstanceup_native(long v, int idx, long up);
	
	public static long sq_getinstanceup(JSqVM v, int idx, long typetag) {
		return sq_getinstanceup_native(v.m_nativeHandle, idx, typetag);
	}
	
	private static native long sq_getinstanceup_native(long v, int idx, long typetag);
	
	public static int sq_setclassudsize(JSqVM v, int idx, int udSize) {
		return sq_setclassudsize_native(v.m_nativeHandle, idx, udSize);
	}
	
	private static native int sq_setclassudsize_native(long vmHaldne, int idx, int udSize);
	
	public static int sq_newclass(JSqVM v, boolean hasBase) {
		return sq_newclass_native(v.m_nativeHandle, hasBase);
	}
	
	private static native int sq_newclass_native(long v, boolean hasBase);
	
	public static int sq_createinstance(JSqVM v, int idx) {
		return sq_createinstance_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_createinstance_native(long v, int idx);
	
	public static int sq_setattributes(JSqVM v, int idx) {
		return sq_setattributes_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_setattributes_native(long v, int idx);
	
	public static int sq_getattributes(JSqVM v, int idx) {
		return sq_getattributes_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_getattributes_native(long v, int idx);
	
	public static int sq_getclass(JSqVM v, int idx) {
		return sq_getclass_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_getclass_native(long v, int idx);
	
	public static void sq_weakref(JSqVM v, int idx) {
		sq_weakref_native(v.m_nativeHandle, idx);
	}
	
	private static native void sq_weakref_native(long v, int idx);
	
	public static int sq_getdefaultdelegate(JSqVM v, JSqObjectType t) {
		return sq_getdefaultdelegate_native(v.m_nativeHandle, t.tag);
	}
	
	private static native int sq_getdefaultdelegate_native(long v, int tag);
	
	// get/set-handle
	
	// TODO Object Manipulation
	
	public static void sq_pushroottable(JSqVM v) {
		sq_pushroottable_native(v.m_nativeHandle);
	}
	
	private static native void sq_pushroottable_native(long v);
	
	public static void sq_pushregistrytable(JSqVM v) {
		sq_pushregistrytable_native(v.m_nativeHandle);
	}
	
	private static native void sq_pushregistrytable_native(long v);
	
	public static void sq_pushconsttable(JSqVM v) {
		sq_pushconsttable_native(v.m_nativeHandle);
	}
	
	private static native void sq_pushconsttable_native(long v);
	
	public static int sq_setroottable(JSqVM v) {
		return sq_setroottable_native(v.m_nativeHandle);
	}
	
	private static native int sq_setroottable_native(long v);
	
	public static int sq_setconsttable(JSqVM v) {
		return sq_setconsttable_native(v.m_nativeHandle);
	}
	
	private static native int sq_setconsttable_native(long v);
	
	public static int sq_newslot(JSqVM v, int idx, boolean bStatic) {
		return sq_newslot_native(v.m_nativeHandle, idx, bStatic);
	}
	
	private static native int sq_newslot_native(long v, int idx, boolean bStatic);
	
	public static int sq_deleteslot(JSqVM v, int idx, boolean pushVal) {
		return sq_deleteslot_native(v.m_nativeHandle, idx, pushVal);
	}
	
	private static native int sq_deleteslot_native(long v, int idx, boolean pushVal);
	
	public static int sq_set(JSqVM v, int idx) {
		return sq_set_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_set_native(long v, int idx);
	
	public static int sq_get(JSqVM v, int idx) {
		return sq_get_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_get_native(long v, int idx);
	
	public static int sq_rawset(JSqVM v, int idx) {
		return sq_rawset_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_rawset_native(long v, int idx);
	
	public static int sq_rawget(JSqVM v, int idx) {
		return sq_rawget_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_rawget_native(long v, int idx);
	
	public static int sq_rawdeleteslot(JSqVM v, int idx, boolean pushVal) {
		return sq_rawdeleteslot_native(v.m_nativeHandle, idx, pushVal);
	}
	
	private static native int sq_rawdeleteslot_native(long v, int idx, boolean pushVal);
	
	public static int sq_newmenber(JSqVM v, int idx, boolean bStatic) {
		return sq_newmember_native(v.m_nativeHandle, idx, bStatic);
	}
	
	private static native int sq_newmember_native(long v, int idx, boolean bStatic);
	
	public static int sq_rawnewmenber(JSqVM v, int idx, boolean bStatic) {
		return sq_rawnewmember_native(v.m_nativeHandle, idx, bStatic);
	}
	
	private static native int sq_rawnewmember_native(long v, int idx, boolean bStatic);
	
	public static int sq_arrayappend(JSqVM v, int idx) {
		return sq_arrayappend_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_arrayappend_native(long v, int idx);
	
	public static int sq_arraypop(JSqVM v, int idx, boolean pushVal) {
		return sq_arraypop_native(v.m_nativeHandle, idx, pushVal);
	}
	
	private static native int sq_arraypop_native(long v, int idx, boolean pushVal);
	
	public static int sq_arrayresize(JSqVM v, int idx, int newSize) {
		return sq_arrayresize_native(v.m_nativeHandle, idx, newSize);
	}
	
	private static native int sq_arrayresize_native(long v, int idx, int newSize);
	
	public static int sq_arrayreverse(JSqVM v, int idx) {
		return sq_arrayreverse_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_arrayreverse_native(long v, int idx);
	
	public static int sq_arrayremove(JSqVM v, int idx, int itemIdx) {
		return sq_arrayremove_native(v.m_nativeHandle, idx, itemIdx);
	}
	
	private static native int sq_arrayremove_native(long v, int idx, int itemIdx);
	
	public static int sq_arrayinsert(JSqVM v, int idx, int destPos) {
		return sq_arrayinsert_native(v.m_nativeHandle, idx, destPos);
	}
	
	private static native int sq_arrayinsert_native(long v, int idx, int destPos);
	
	public static int sq_setdelegate(JSqVM v, int idx) {
		return sq_setdelegate_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_setdelegate_native(long v, int idx);
	
	public static int sq_getdelegate(JSqVM v, int idx) {
		return sq_getdelegate_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_getdelegate_native(long v, int idx);
	
	public static int sq_clone(JSqVM v, int idx) {
		return sq_clone_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_clone_native(long v, int idx);
	
	public static int sq_setfreevariable(JSqVM v, int idx, long nVal) {
		return sq_setfreevariable_native(v.m_nativeHandle, idx, nVal);
	}
	
	private static native int sq_setfreevariable_native(long v, int idx, long nVal);
	
	public static int sq_next(JSqVM v, int idx) {
		return sq_next_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_next_native(long v, int idx);
	
	public static int sq_getweakrefval(JSqVM v, int idx) {
		return sq_getweakrefval_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_getweakrefval_native(long v, int idx);
	
	public static int sq_clear(JSqVM v, int idx) {
		return sq_clear_native(v.m_nativeHandle, idx);
	}
	
	private static native int sq_clear_native(long v, int idx);
	
	// TODO Calls
	
	public static int sq_call(JSqVM v, int numParams, boolean retval, boolean raiseError) {
		return sq_call_native(v.m_nativeHandle, numParams, retval, raiseError);
	}
	
	private static native int sq_call_native(long v, int numParams, boolean retval, boolean raiseError);
	
	public static int sq_resume(JSqVM v, boolean retval, boolean raiseError) {
		return sq_resume_native(v.m_nativeHandle, retval, raiseError);
	}
	
	private static native int sq_resume_native(long v, boolean retval, boolean raiseError);
	
	public static String sq_getlocal(JSqVM v, long level, long idx) {
		return sq_getlocal_native(v.m_nativeHandle, level, idx);
	}
	
	private static native String sq_getlocal_native(long v, long level, long idx);
	
	public static int sq_getcallee(JSqVM v) {
		return sq_getcallee_native(v.m_nativeHandle);
	}
	
	private static native int sq_getcallee_native(long v);
	
	public static String sq_getfreevariable(JSqVM v, int idx, long nVal) {
		return sq_getfreevariable_native(v.m_nativeHandle, idx, nVal);
	}
	
	private static native String sq_getfreevariable_native(long v, int idx, long nVal);
	
	public static int sq_throwerror(JSqVM v, String err) {
		return sq_throwerror_native(v.m_nativeHandle, err);
	}
	
	private static native int sq_throwerror_native(long v, String err);
	
	public static int sq_throwobject(JSqVM v) {
		return sq_throwobject_native(v.m_nativeHandle);
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
	
	public static long sq_objtouserpointer(JSqObject obj) {
		return sq_objtouserpointer_native(obj.m_nativeHandle);
	}
	
	private static native long sq_objtouserpointer_native(long obj);
	
	public static long sq_getobjtypetag(JSqObject obj) {
		return sq_getobjtypetag_native(obj.m_nativeHandle);
	}
	
	private static native long sq_getobjtypetag_native(long obj);
	
	// GC
	
	public static int sq_collectgarbage(JSqVM v) {
		return sq_collectgarbage_native(v.m_nativeHandle);
	}
	
	private static native int sq_collectgarbage_native(long v);
	
	public static int sq_resurrectunreachable(JSqVM v) {
		return sq_resurrectunreachable_native(v.m_nativeHandle);
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
