/**********************************************
 * Copyright (C) 2010 Lukas Laag
 * This file is part of vectomatic2.
 * 
 * vectomatic2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * vectomatic2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with vectomatic2.  If not, see http://www.gnu.org/licenses/
 **********************************************/
package ch.bergturbenthal.hs485.frontend.gwtfrontend.client.svg;

/**
 * Class to split SVG attribute values into a stream of tokens. Each token is
 * either a piece of text to be quoted verbatim by the SVG id normalizer, or an
 * idref to be replaced by a normalized id ref.
 * 
 * @author laaglu
 */
public class IdRefTokenizer {
	static class IdRefToken {
		public static final int	DATA	= 2;
		public static final int	IDREF	= 1;
		int											kind;
		String									value;

		public int getKind() {
			return kind;
		}

		public String getValue() {
			return value;
		}
	}

	protected static final String	END		= ")";
	protected static final String	START	= "url(#";
	protected int									index1;
	protected int									index2;
	protected String							str;
	protected IdRefToken					token	= new IdRefToken();

	public IdRefToken nextToken() {
		if (index1 != str.length()) {
			if (token.kind == IdRefToken.IDREF) {
				token.kind = IdRefToken.DATA;
				index2 = str.indexOf(START, index1);
				if (index2 != -1) {
					token.value = str.substring(index1, index2 + START.length());
					index1 = index2 + START.length();
				} else {
					token.value = str.substring(index1);
					index1 = str.length();
				}
			} else {
				index2 = str.indexOf(END, index1);
				if (index2 != -1) {
					token.value = str.substring(index1, index2);
					index1 = index2;
					token.kind = IdRefToken.IDREF;
				} else {
					token.value = str.substring(index1);
					index1 = str.length();
				}
			}
			return token;
		}
		return null;
	}

	public void tokenize(final String str) {
		this.str = str;
		index1 = 0;
		index2 = 0;
		token.kind = IdRefToken.IDREF;
	}
}