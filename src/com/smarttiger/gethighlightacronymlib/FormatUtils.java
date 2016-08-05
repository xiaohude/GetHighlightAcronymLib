package com.smarttiger.gethighlightacronymlib;

import android.database.CharArrayBuffer;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import com.smarttiger.gethighlightacronymlib.HanziToPinyin.Token;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Assorted utility methods related to text formatting in Contacts.
 */
public class FormatUtils {

    /**
     * Finds the earliest point in buffer1 at which the first part of buffer2 matches.  For example,
     * overlapPoint("abcd", "cdef") == 2.
     */
    public static int overlapPoint(CharArrayBuffer buffer1, CharArrayBuffer buffer2) {
        if (buffer1 == null || buffer2 == null) {
            return -1;
        }
        return overlapPoint(Arrays.copyOfRange(buffer1.data, 0, buffer1.sizeCopied),
                Arrays.copyOfRange(buffer2.data, 0, buffer2.sizeCopied));
    }

    /**
     * Finds the earliest point in string1 at which the first part of string2 matches.  For example,
     * overlapPoint("abcd", "cdef") == 2.
     */
    public static int overlapPoint(String string1, String string2) {
        if (string1 == null || string2 == null) {
            return -1;
        }
        return overlapPoint(string1.toCharArray(), string2.toCharArray());
    }

    /**
     * Finds the earliest point in array1 at which the first part of array2 matches.  For example,
     * overlapPoint("abcd", "cdef") == 2.
     */
    public static int overlapPoint(char[] array1, char[] array2) {
        if (array1 == null || array2 == null) {
            return -1;
        }
        int count1 = array1.length;
        int count2 = array2.length;

        // Ignore matching tails of the two arrays.
        while (count1 > 0 && count2 > 0 && array1[count1 - 1] == array2[count2 - 1]) {
            count1--;
            count2--;
        }

        int size = count2;
        for (int i = 0; i < count1; i++) {
            if (i + size > count1) {
                size = count1 - i;
            }
            int j;
            for (j = 0; j < size; j++) {
                if (array1[i+j] != array2[j]) {
                    break;
                }
            }
            if (j == size) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Applies the given style to a range of the input CharSequence.
     * @param style The style to apply (see the style constants in {@link Typeface}).
     * @param input The CharSequence to style.
     * @param start Starting index of the range to style (will be clamped to be a minimum of 0).
     * @param end Ending index of the range to style (will be clamped to a maximum of the input
     *     length).
     * @param flags Bitmask for configuring behavior of the span.  See {@link android.text.Spanned}.
     * @return The styled CharSequence.
     */
    public static CharSequence applyStyleToSpan(int style, CharSequence input, int start, int end,
            int flags) {
        // Enforce bounds of the char sequence.
        start = Math.max(0, start);
        end = Math.min(input.length(), end);
        SpannableString text = new SpannableString(input);
        text.setSpan(new StyleSpan(style), start, end, flags);
        return text;
    }

    public static void copyToCharArrayBuffer(String text, CharArrayBuffer buffer) {
        if (text != null) {
            char[] data = buffer.data;
            if (data == null || data.length < text.length()) {
                buffer.data = text.toCharArray();
            } else {
                text.getChars(0, text.length(), data, 0);
            }
            buffer.sizeCopied = text.length();
        } else {
            buffer.sizeCopied = 0;
        }
    }

    /** Returns a String that represents the content of the given {@link CharArrayBuffer}. */
    public static String charArrayBufferToString(CharArrayBuffer buffer) {
        return new String(buffer.data, 0, buffer.sizeCopied);
    }

    /**
     * Finds the index of the first word that starts with the given prefix.
     * <p>
     * If not found, returns -1.
     *
     * @param text the text in which to search for the prefix
     * @param prefix the text to find, in upper case letters
     */
    public static int indexOfWordPrefix(CharSequence text, String prefix) {
        if (prefix == null || text == null) {
            return -1;
        }

        int textLength = text.length();
        int prefixLength = prefix.length();

        if (prefixLength == 0 || textLength < prefixLength) {
            return -1;
        }

        int i = 0;
        while (i < textLength) {
            // Skip non-word characters
            while (i < textLength && !Character.isLetterOrDigit(text.charAt(i))) {
                i++;
            }

            if (i + prefixLength > textLength) {
                return -1;
            }

            // Compare the prefixes
            int j;
            for (j = 0; j < prefixLength; j++) {
                if (Character.toUpperCase(text.charAt(i + j)) != prefix.charAt(j)) {
                    break;
                }
            }
            if (j == prefixLength) {
                return i;
            }

            i++;
        }

        return -1;
    }

    
    /**
     * 匹配电话号码字符串。自动过滤'-'和' '。
     * @param text 原字符串
     * @param prefix 高亮字符串
     * @return HighlightIndex
     * @author zhuxh2
     * */
    public static HighlightIndex indexOfNumber(CharSequence text, String prefix) {
        if (prefix == null || text == null) {
            return null;
        }
        int textLength = text.length();
        int prefixLength = prefix.length();
        if (prefixLength == 0 || textLength < prefixLength) {
            return null;
        }
        int i = 0;
        while (i < textLength) {
        	int end = 0;
        	//跳过开头不是数字的。
            while (i < textLength && !Character.isDigit(text.charAt(i))) {
                i++;
            }
            if (i + prefixLength > textLength) {
                return null;
            }
            // Compare the prefixes
            int j;
            for (j = 0; j < prefixLength; j++) {
            	while ( (i+end + j != textLength) && (text.charAt(i+end + j) == '-' || text.charAt(i+end + j) == ' ') )
            	{
        			end ++;	
            	}
    			if(i+end + j == textLength)
            		break;
                if (text.charAt(i+end + j) != prefix.charAt(j)) {
                    break;
                }
            }
            if (j == prefixLength) {
            	return new HighlightIndex(i, i + prefixLength + end);
            }
            i++;
        }
        return null;
    }
    

    /**
     * 获取首字母缩略字符匹配的下标
     * @param input 原字符串
     * @param prefix 高亮字符串
     * @return 原字符串需要高亮的下标位置（start和end）List
     * @author zhuxh2
     * */
    public static ArrayList<HighlightIndex> getAcronym(String input, String prefix) {
    	
    	System.out.println("---------------------------------------");
    	System.out.println("input=="+input+"--prefix=="+prefix);
    	
		ArrayList<Token> tokens = HanziToPinyin.getInstance().getTokens(input);
		//姓名缩写
		StringBuilder acronym = new StringBuilder();
		//缩写字母index
		ArrayList<HighlightIndex> indexs = new ArrayList<HighlightIndex>();
		//匹配后的缩写字母index
		ArrayList<HighlightIndex> indexsResult = new ArrayList<HighlightIndex>();
		
		int index = 0;
		if (tokens != null && tokens.size() > 0) {
			for (Token token : tokens) {
				if (Token.PINYIN == token.type) {
					acronym.append(token.target.charAt(0));
					indexs.add(new HighlightIndex(index, index+1));
					index++;
				} else {
					acronym.append(token.source.charAt(0));
					indexs.add(new HighlightIndex(index, index+1));
					index = index + token.source.length();
					while(index < input.length() && input.charAt(index) == ' ')
						index++;
				}
			}
			int start = indexOfWordPrefix(acronym, prefix);
			if(start != -1)
				for(int j = start; j < start + prefix.length(); j++) {
					indexsResult.add(indexs.get(j));
					System.out.println("indexs.get(j)===="+indexs.get(j).start+","+indexs.get(j).end);
				}
				
		}
		return indexsResult;
	}
    
    /**
     * 智能匹配姓名。中文可匹配拼音首字母，英文可匹配缩写。
     * @param input 原字符串
     * @param prefix 高亮字符串
     * @return 原字符串需要高亮的下标位置（start和end）List
     * @author zhuxh2
     * */
    public static ArrayList<HighlightIndex> indexOfName(CharSequence text, String prefix) {
    	
        ArrayList<HighlightIndex> highlightSequenceList = new ArrayList<HighlightIndex>();
        
        int start = indexOfWordPrefix(text, prefix);
        if(start != -1)
        	highlightSequenceList.add(new HighlightIndex(start, start+prefix.length()));
        else
        	highlightSequenceList = getAcronym(text.toString(), prefix.toUpperCase());
        System.out.println("highlightSequenceList.size()======="+highlightSequenceList.size());
        return highlightSequenceList;
    }
    
    
    /**
     * 供SortCursor使用
     * 智能匹配姓名。中文可匹配拼音首字母，英文可匹配缩写。
     * @return 匹配成功的第一个字符下标
     * */
    public static int indexOfNameForSort(CharSequence text, String prefix) {
    	
        ArrayList<HighlightIndex> highlightSequenceList = new ArrayList<HighlightIndex>();
        
        int start = indexOfWordPrefix(text, prefix);
        if(start != -1)
        	return start;
        else
        	highlightSequenceList = getAcronym(text.toString(), prefix.toUpperCase());
        
        if(highlightSequenceList.size() == 0)
        	return -1;
        else {
            System.out.println("highlightSequenceList.get(0).start======="+highlightSequenceList.get(0).start);
        	return highlightSequenceList.get(0).start;
        }
    }
    
}
