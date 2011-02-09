package wordcram;

/*
 Copyright 2010 Daniel Bernier

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import processing.core.PFont;

class WordShaper {
	private FontRenderContext frc = new FontRenderContext(null, true, true);
	
	Shape getShapeFor(EngineWord eWord) {

		Shape shape = makeShape(eWord.word.word, eWord.getFont(), eWord.getSize());
		
		if (isTooSmall(shape)) {
			return null;		
		}
		
		return moveToOrigin(
				rotate(shape, eWord.getAngle()));
	}

	private Shape makeShape(String word, PFont pFont, float fontSize) {
		Font font = pFont.getFont().deriveFont(fontSize);
		
		char[] chars = word.toCharArray();
		
		// TODO hmm: this doesn't render newlines.  Hrm.  If you're word text is "foo\nbar", you get "foobar".
		GlyphVector gv = font.layoutGlyphVector(frc, chars, 0, chars.length,
				Font.LAYOUT_LEFT_TO_RIGHT);

		return gv.getOutline();
	}
	
	private boolean isTooSmall(Shape shape) {
		Rectangle2D r = shape.getBounds2D();
		
		// TODO extract config setting for minWordRenderedSize, and take height into account -- not just width.
		// Note, however, that this is called BEFORE rotate(), so the only words like "I" run the risk of 
		// being unfairly rejected if we don't consider height.
		// Though, since we say "too narrow OR too short", rather than "...AND...", words like "I"
		// would be unfairly rejected anyway.
		
		int minSize = 7;
		
		return r.getWidth() < minSize || r.getHeight() < minSize;
	}
	
	private Shape rotate(Shape shape, float rotation) {
		if (rotation == 0) {
			return shape;
		}

		return AffineTransform.getRotateInstance(rotation).createTransformedShape(shape);
	}

	private Shape moveToOrigin(Shape shape) {
		Rectangle2D rect = shape.getBounds2D();
		
		if (rect.getX() == 0 && rect.getY() == 0) {
			return shape;
		}
		
		return AffineTransform.getTranslateInstance(-rect.getX(), -rect.getY()).createTransformedShape(shape);
	}
}