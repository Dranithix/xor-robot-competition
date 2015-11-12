package com.xor.controller;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;

import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

public class GraphFilter {
	private Array<Vector2> lastFilteredData;
	private FloatArray filteredAreas = new FloatArray();

	public Array<Vector2> filterGraph(float[] data) {
		data = medianFilter(data, 5);

		Array<Vector2> filteredData = new Array<Vector2>();
		for (int i = 0; i < data.length; i++) {
			filteredData.add(new Vector2(i, (float) data[i]));
		}

		int[] filteredIndices = douglasPeuckerFilter(data, 3);

		Iterator<Vector2> it = filteredData.iterator();
		int removeIndex = 0;
		while (it.hasNext()) {
			it.next();
			if (filteredIndices[removeIndex] != 0 && removeIndex != 128)
				it.remove();
			removeIndex++;
		}

		filteredData = bresenham(filteredData);
		if (lastFilteredData != null) {
			for (int i = 0; i < filteredData.size; i++) {
				filteredData.set(
						i,
						filteredData.get(i).interpolate(
								lastFilteredData.get(i), 0.55f,
								Interpolation.circle));
			}
		}
		lastFilteredData = new Array<Vector2>(filteredData);

		calculateAreas(filteredData);
		return filteredData;
	}

	Bresenham2 bresenham = new Bresenham2();

	private Array<Vector2> bresenham(Array<Vector2> points) {
		Array<Vector2> result = new Array<Vector2>();
		for (int i = 1; i < points.size; i++) {
			Vector2 current = points.get(i);
			Vector2 last = points.get(i - 1);
			if ((int) current.x - (int) last.x > 0) {
				for (GridPoint2 point : bresenham.line(new GridPoint2(
						(int) last.x, (int) last.y), new GridPoint2(
						(int) current.x, (int) current.y))) {
					if (result.size == point.x) {
						result.add(new Vector2(point.x, point.y));
					}
				}
			}
		}
		return result;
	}

	private void calculateAreas(Array<Vector2> signal) {
		filteredAreas.clear();
		
		float[] buff = new float[84];
		for (int i = 0, x = 0; i < 42; i++) {
			buff[x++] = i;
			buff[x++] = signal.get(i).y;
		}
		buff[1] = 0;
		buff[83] = 0;
		
		filteredAreas.add(Math.abs(new Polygon(buff).area()));

		buff = new float[88];
		for (int i = 42, x = 0; i < 86; i++) {
			buff[x++] = i;
			buff[x++] = signal.get(i).y;
		}
		buff[1] = 0;
		buff[87] = 0;
		
		filteredAreas.add(Math.abs(new Polygon(buff).area()));

		buff = new float[84];
		for (int i = 86, x = 0; i < 128; i++) {
			buff[x++] = i;
			buff[x++] = signal.get(i).y;
		}
		buff[1] = 0;
		buff[83] = 0;
		
		filteredAreas.add(Math.abs(new Polygon(buff).area()));
	}

	public FloatArray getFilteredAreas() {
		return filteredAreas;
	}

	private float[] medianFilter(float[] signal, int windowLen) {

		float[] result = new float[signal.length];
		for (int i = windowLen; i < signal.length - (windowLen / 2); i++) {
			float window[] = new float[windowLen];
			for (int j = 0; j < windowLen; ++j) {
				window[j] = signal[i - (windowLen / 2) + j];
			}
			Arrays.sort(window);
			result[i - (windowLen / 2)] = window[windowLen / 2];
		}
		return result;
	}

	private int[] douglasPeuckerFilter(float[] points, float epsilon) {
		Stack<SimpleEntry<Integer, Integer>> stk = new Stack<SimpleEntry<Integer, Integer>>();
		stk.add(new SimpleEntry<Integer, Integer>(0, points.length - 1));

		int globalStartIndex = 0;
		int[] bitArray = new int[points.length];

		int startIndex = 0, lastIndex = points.length - 1;
		while (stk.size() > 0) {
			startIndex = stk.peek().getKey();
			lastIndex = stk.peek().getValue();
			stk.pop();

			float dmax = 0f;
			int index = startIndex;

			float a = lastIndex - startIndex;
			float b = points[lastIndex] - points[startIndex];
			float c = -(b * startIndex - a * points[startIndex]);
			float norm = (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));

			for (int i = index + 1; i < lastIndex; ++i) {
				if (bitArray[i - globalStartIndex] == 0) {
					float distance = Math.abs(b * i - a * points[i] + c) / norm;
					if (distance > dmax) {
						index = i;
						dmax = distance;
					}
				}
			}

			if (dmax > epsilon) {
				stk.push(new SimpleEntry<Integer, Integer>(startIndex, index));
				stk.push(new SimpleEntry<Integer, Integer>(index, lastIndex));
			} else {
				for (int i = startIndex + 1; i < lastIndex; ++i) {
					bitArray[i - globalStartIndex] = 1;
				}
			}
		}
		return bitArray;
	}
}
