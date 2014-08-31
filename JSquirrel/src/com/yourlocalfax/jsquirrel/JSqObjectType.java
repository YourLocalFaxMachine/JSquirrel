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

import static com.yourlocalfax.jsquirrel.Squirrel.*;

public enum JSqObjectType {
	
	Null(RT_NULL | SQOBJECT_CANBEFALSE),
	Integer(RT_INTEGER | SQOBJECT_NUMERIC | SQOBJECT_CANBEFALSE),
	Float(RT_FLOAT | SQOBJECT_NUMERIC | SQOBJECT_CANBEFALSE),
	Bool(RT_BOOL | SQOBJECT_CANBEFALSE),
	String(RT_STRING | SQOBJECT_REF_COUNTED),
	Table(RT_TABLE | SQOBJECT_REF_COUNTED | SQOBJECT_DELEGABLE),
	Array(RT_ARRAY | SQOBJECT_REF_COUNTED),
	Userdata(RT_USERDATA | SQOBJECT_REF_COUNTED | SQOBJECT_DELEGABLE),
	Closure(RT_CLOSURE | SQOBJECT_REF_COUNTED),
	NativeClosure(RT_NATIVECLOSURE | SQOBJECT_REF_COUNTED),
	Generator(RT_GENERATOR | SQOBJECT_REF_COUNTED),
	UserPointer(RT_USERPOINTER),
	Thread(RT_THREAD | SQOBJECT_REF_COUNTED),
	FuncProto(RT_FUNCPROTO | SQOBJECT_REF_COUNTED),
	Class(RT_CLASS | SQOBJECT_REF_COUNTED),
	Instance(RT_INSTANCE | SQOBJECT_REF_COUNTED | SQOBJECT_DELEGABLE),
	WeakRef(RT_WEAKREF | SQOBJECT_REF_COUNTED),
	Outer(RT_OUTER | SQOBJECT_REF_COUNTED);

	static JSqObjectType getFromValue(int tag) {
		for (JSqObjectType o : JSqObjectType.values())
			if (o.tag == tag)
				return o;
		return Null;
	}
	
	public boolean isRefCounted() {
		return (tag & SQOBJECT_REF_COUNTED) == SQOBJECT_REF_COUNTED;
	}
	
	public final int tag;
	
	JSqObjectType(int value) {
		tag = value;
	}
	
}
