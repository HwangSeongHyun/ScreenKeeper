
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

@SuppressWarnings("serial")
public class ScreenKeeper extends JFrame {
	private JFrame frame = this;
	private BufferedImage img = null;
	private ImageIcon icon;
	private JButton choose = new JButton("잠금");
	private JPanel panel;
	private LockOpenActionListener loa = new LockOpenActionListener();
	private UnlockOpenActionListener uoa = new UnlockOpenActionListener();
	private String filePath;

	public ScreenKeeper() {

		
		try {
			panel = new JPanel() {
				public void paintComponent(Graphics g) {
					if (icon == null) {
						g.drawImage(null, 0, 0, null);
					} else {
						g.drawImage(icon.getImage(), 0, 0, null);
					}
					setOpaque(false);
					super.paintComponent(g);
				}
			};
			choose.addActionListener(loa);
			choose.setSize(200, 100);
			panel.add(choose);
			add(panel);
			setUndecorated(true);
			setFocusable(true);
			requestFocus();
			setExtendedState(JFrame.MAXIMIZED_BOTH);
			setVisible(true);
			setResizable(false);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// File Open Action Listener
	class LockOpenActionListener implements ActionListener {
		private JFileChooser chooser;

		public LockOpenActionListener() {
			chooser = new JFileChooser("C:\\Users\\82103\\eclipse-workspace\\ScreenKeeper");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			KeyStopper.create(frame);
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
			chooser.setFileFilter(filter);

			int ret = chooser.showOpenDialog(null);
			if (ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다", "경고", JOptionPane.WARNING_MESSAGE);
				return;
			} else {
				filePath = chooser.getSelectedFile().getPath();
				try {
					img = ImageIO.read(new File(filePath));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			icon = new ImageIcon(filePath);
			choose.setText("잠금해제");
			choose.removeActionListener(loa);
			choose.addActionListener(uoa);
			addKeyListener(new KeyboardActionListener());
			repaint();
		}
	}

	class UnlockOpenActionListener implements ActionListener {
		private JFileChooser chooser;

		public UnlockOpenActionListener() {
			chooser = new JFileChooser("C:\\Users\\82103\\eclipse-workspace\\ScreenKeeper");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String filePath;
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
			chooser.setFileFilter(filter);

			int ret = chooser.showOpenDialog(null);
			if (ret != JFileChooser.APPROVE_OPTION) {
				JOptionPane.showMessageDialog(null, "파일을 선택하지 않았습니다", "경고", JOptionPane.WARNING_MESSAGE);
				return;
			} else {
				filePath = chooser.getSelectedFile().getPath();
				try {
					img = ImageIO.read(new File(filePath));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			Image img1 = icon.getImage();
			Image img2 = new ImageIcon(filePath).getImage();
			int width1 = img1.getWidth(null);
			int height1 = img1.getHeight(null);
			int width2 = img2.getWidth(null);
			int height2 = img2.getHeight(null);
			int[] pixels1 = new int[width1 * height1];
			int[] pixels2 = new int[width2 * height2];
			PixelGrabber grab1 = new PixelGrabber(img1, 0, 0, width1, height1, pixels1, 0, width1);
			PixelGrabber grab2 = new PixelGrabber(img2, 0, 0, width1, height1, pixels2, 0, width1);
			try {
				grab1.grabPixels();
				grab2.grabPixels();
				int[][] picture1 = new int[width1][height1];
				int[][] picture2 = new int[width2][height2];
				if (pixels1.length != pixels2.length) {
					JOptionPane.showMessageDialog(null, "이미지가 일치하지 않습니다", "경고", JOptionPane.WARNING_MESSAGE);
					return;
				} 
				for (int i = 0; i < pixels1.length; i++) {
					picture1[i % width1][i / width1] = pixels1[i];
					picture2[i % width2][i / width2] = pixels2[i];
					if (picture1[i % width1][i / width1] == picture2[i % width2][i / width2]) {
						continue;
					} else {
						JOptionPane.showMessageDialog(null, "이미지가 일치하지 않습니다", "알림", JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
				JOptionPane.showMessageDialog(null, "이미지가 일치합니다 프로그램을 종료합니다", "알림", JOptionPane.WARNING_MESSAGE);
				System.exit(1);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
		}
	}

	public static class KeyStopper implements Runnable {
		private boolean working = true;
		private JFrame frame;

		public KeyStopper(JFrame frame) {
			this.frame = frame;
		}

		public void stop() {
			working = false;
		}

		public static KeyStopper create(JFrame frame) {
			KeyStopper stopper = new KeyStopper(frame);
			new Thread(stopper, "Key Stopper").start();
			return stopper;
		}

		public void run() {
			try {
				Robot robot = new Robot();
				while (working) {
					robot.keyRelease(KeyEvent.VK_WINDOWS);
					robot.keyRelease(KeyEvent.VK_D);
					robot.keyRelease(KeyEvent.VK_L);
					robot.keyRelease(KeyEvent.VK_R);
					robot.keyRelease(KeyEvent.VK_ESCAPE);
					robot.keyRelease(KeyEvent.VK_CONTROL);
					robot.keyRelease(KeyEvent.VK_SHIFT);
					robot.keyRelease(KeyEvent.VK_ALT);
					robot.keyRelease(KeyEvent.VK_TAB);
					frame.requestFocus();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	// Keyboard Press Action Listener
	class KeyboardActionListener extends KeyAdapter {

		public void keyPressed(KeyEvent e) {
			JOptionPane.showMessageDialog(null, "키 입력 불가!", "경고", JOptionPane.WARNING_MESSAGE);
			return;
		}

	}

	public static void main(String[] args) {
		new ScreenKeeper();
	}
}