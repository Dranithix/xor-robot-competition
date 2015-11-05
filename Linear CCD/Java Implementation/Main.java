import java.awt.Color;
import java.awt.Graphics;
import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Main extends JFrame {
	double inputSignal[], outputSignal[];
	private boolean log = true;

	public static void main(String[] args) {
		new Main();
	}

	public Main() {
		super("XOR - Linear CCD Analysis");

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setContentPane(new DrawPanel());
		setSize(800, 600);

		setVisible(true);

		while (true) {
			inputSignal = generateNoisySignal(128);
			outputSignal = new double[128];

			System.arraycopy(inputSignal, 0, outputSignal, 0,
					inputSignal.length);

			long startTime = 0;

			startTime = System.nanoTime();
			outputSignal = median(outputSignal, 10);
			int[] remove = douglasPeucker(outputSignal);
			for (int i = 0; i < remove.length; i++) {
				if (remove[i] != 0)
					outputSignal[i] = -1.0;
			}
			if (log)
				System.out.println("Filter Processing Time: "
						+ (new BigDecimal(System.nanoTime() - startTime)
								.divide(new BigDecimal(1000000000)))
						+ " seconds.");

			calculateAreas(outputSignal);

			if (log)
				System.out.println(Arrays.toString(outputSignal)
						+ System.lineSeparator());

			repaint();

			try {
				Thread.sleep(500);
			} catch (Exception ex) {
			}
		}
	}

	private double[] generateNoisySignal(int len) {
		double[] signal = new double[len];
		for (int i = 0; i < len; i++) {
			signal[i] = Math.max(0, new Random().nextInt(156)); // Pure noise
																// signal
			// signal[i] = Math.max(0, new Random().nextInt(156) - i); //
			// Descending noise signal
		}
		return signal;
	}

	class DrawPanel extends JPanel {
		public void paintComponent(Graphics g) {
			List<Integer> xs = new ArrayList<Integer>();
			List<Integer> ys = new ArrayList<Integer>();
			for (double i = 0; i < inputSignal.length; i++) {
				if (inputSignal[(int) i] != -1.0) {
					xs.add((int) ((i / inputSignal.length) * getWidth()));
					ys.add(getHeight()
							- (int) ((inputSignal[(int) i] / 156.0) * getHeight()));
				}
			}
			g.setColor(Color.green);
			g.drawPolyline(xs.stream().mapToInt(i -> i).toArray(), ys.stream()
					.mapToInt(i -> i).toArray(), xs.size());

			xs.clear();
			ys.clear();

			for (double i = 0; i < outputSignal.length; i++) {
				if (outputSignal[(int) i] != -1.0) {
					xs.add((int) ((i / outputSignal.length) * getWidth()));
					ys.add(getHeight()
							- (int) ((outputSignal[(int) i] / 156.0) * getHeight()));
				}
			}
			g.setColor(Color.black);
			g.drawPolyline(xs.stream().mapToInt(i -> i).toArray(), ys.stream()
					.mapToInt(i -> i).toArray(), xs.size());
		}
	}

	public void calculateAreas(double[] signal) {
		double[] leftPartition = new double[(signal.length - 1) / 2], rightPartition = new double[(signal.length - 1) / 2];
		System.arraycopy(signal, 0, leftPartition, 0, (signal.length - 1) / 2);
		System.arraycopy(signal, signal.length / 2, rightPartition, 0,
				(signal.length - 1) / 2);

		double leftAreaSum = 0, rightAreaSum = 0;

		int lastPossibleIndex = -1;
		for (int i = 0; i < leftPartition.length - 1; i++) {
			if (leftPartition[i] == -1.0)
				continue;
			else {
				if (lastPossibleIndex == -1) {
					lastPossibleIndex = i;
					continue;
				}
				int trpHeight = (i + 1) - lastPossibleIndex;
				double area = (leftPartition[i] + leftPartition[lastPossibleIndex])
						/ 2 * trpHeight;
				leftAreaSum += area;
				lastPossibleIndex = i;
			}
		}

		lastPossibleIndex = -1;
		for (int i = 0; i < rightPartition.length - 1; i++) {
			if (rightPartition[i] == -1.0)
				continue;
			else {
				if (lastPossibleIndex == -1) {
					lastPossibleIndex = i;
					continue;
				}
				int trpHeight = (i + 1) - lastPossibleIndex;
				double area = (rightPartition[i] + rightPartition[lastPossibleIndex])
						/ 2 * trpHeight;
				rightAreaSum += area;
				lastPossibleIndex = i;
			}
		}
		double ratio = leftAreaSum / rightAreaSum;

		if (Double.isFinite(ratio))
			System.out.println("Left Area to Right Area Ratio: " + ratio);
	}

	public double[] median(double[] signal, int windowLen) {

		double[] result = new double[signal.length];
		for (int i = windowLen; i < signal.length - (windowLen / 2); i++) {
			double window[] = new double[windowLen];
			for (int j = 0; j < windowLen; ++j) {
				window[j] = signal[i - (windowLen / 2) + j];
			}
			Arrays.sort(window);
			result[i - (windowLen / 2)] = window[windowLen / 2];
		}
		return result;
	}

	private double epsilon = 10;

	public int[] douglasPeucker(double[] points) {
		Stack<SimpleEntry<Integer, Integer>> stk = new Stack<SimpleEntry<Integer, Integer>>();
		stk.add(new SimpleEntry<Integer, Integer>(0, points.length - 1));

		int globalStartIndex = 0;
		int[] bitArray = new int[points.length];

		int startIndex = 0, lastIndex = points.length - 1;
		while (stk.size() > 0) {
			startIndex = stk.peek().getKey();
			lastIndex = stk.peek().getValue();
			stk.pop();

			double dmax = 0f;
			int index = startIndex;

			double a = lastIndex - startIndex;
			double b = points[lastIndex] - points[startIndex];
			double c = -(b * startIndex - a * points[startIndex]);
			double norm = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));

			for (int i = index + 1; i < lastIndex; ++i) {
				if (bitArray[i - globalStartIndex] == 0) {
					double distance = Math.abs(b * i - a * points[i] + c)
							/ norm;
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
