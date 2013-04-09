/*
 * The MIT License
 * Copyright (c) 2013 Vitalik Lim (lim.vitaliy@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.cryart.hopepsalms;

import java.util.List;
import android.content.Context;

public class SimpleFilterableAdapter<ObjectType> extends FilterableAdapter<ObjectType, String> {
 
    public SimpleFilterableAdapter(Context context) {
		super(context);
	}
    
    public SimpleFilterableAdapter(Context context, int resourceId) {
        super(context, resourceId, 0, null);
    }
    
    public SimpleFilterableAdapter(Context context, List<ObjectType> objects) {
        super(context, 0, 0, objects);
    }
    
    public SimpleFilterableAdapter(Context context, int resourceId, List<ObjectType> objects) {
        super(context, resourceId, 0, objects);
    }
    
    public SimpleFilterableAdapter(Context context, int resourceId, int textResourceId, List<ObjectType> objects) {
    	super(context, resourceId, textResourceId, objects);
    }

	@Override
    protected String prepareFilter(CharSequence seq) {
        return seq.toString().toLowerCase();
    }
	
	/*
	 * Overriding main filtering function
	 * 
	 * Note, that this is straightforward approach to search
	 * for occurrences without word boundaries. 
	 * 
	 * Written for quick lookup of the hymnals
	 * 
	 */
    @Override
    protected boolean passesFilter(ObjectType object, String constraint) {
        String repr = object.toString().toLowerCase().trim();
        
        // Initial check of the whole
        // string as the substring
        // of any item in the object list
        if (repr.contains(constraint))
            return true;
        else {
        	// If the whole substring is not found
        	// splitting it into words and trying
        	// to see if all the words are part of
        	// any item in the object list
            final String[] words = constraint.split(" ");
	        final int wordCount = words.length;
	        
	        for (int i = 0; i < wordCount; i++) {
	        	if (!repr.contains(words[i])){
	        		return false;
	        	}
	        }
	        return true;
	    }
    }
}