package com.b2beyond.wallet.b2bcoin.view.view;


import com.b2beyond.wallet.b2bcoin.view.model.ChangeSize;
import com.b2beyond.wallet.rpc.RpcPoller;
import com.b2beyond.wallet.rpc.model.*;
import com.b2beyond.wallet.rpc.model.Error;
import com.b2beyond.wallet.rpc.model.coin.BlockCount;
import com.b2beyond.wallet.b2bcoin.util.B2BUtil;
import com.b2beyond.wallet.b2bcoin.view.TabContainer;
import com.b2beyond.wallet.b2bcoin.view.controller.ActionController;
import com.b2beyond.wallet.rpc.model.coin.BlockHeaderWrapper;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import org.apache.commons.configuration.PropertiesConfiguration;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class MainFrame extends JFrame implements Observer {

    private JPanel content;
    private JPanel menu;

    private ActionController actionController;

    private JLabel dataSynchronizingBlocks;
    private JProgressBar progressBar = new JProgressBar();

    private List<JButton> menus;

    private boolean firstUpdate = true;
    private long blockChucksFetched = 0;
    private boolean restarting;

    private boolean canStopWalletIfNotSyncedYet = false;

    protected List<RpcPoller> walletRpcPollers = new ArrayList<>();

    /**
     * Create the frame
     */
    public MainFrame(PropertiesConfiguration applicationProperties, ActionController actionController) {
        this.actionController = actionController;

        this.setTitle("B2BCoin GUI");
        this.setBackground(B2BUtil.mainColor);

        URL splashScreenLocation = Thread.currentThread().getContextClassLoader().getResource("my-logo.ico");
        if (splashScreenLocation != null) {
            this.setIconImage(new ImageIcon(splashScreenLocation).getImage());
        }

        Dimension minimumSize = new Dimension(applicationProperties.getInt("min-width"), applicationProperties.getInt("min-height"));
        this.setMinimumSize(minimumSize);
        this.setPreferredSize(minimumSize);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //This will center the JFrame in the middle of the screen
        this.setLocationRelativeTo(null);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        JDesktopPane desktopPane = new JDesktopPane();
        //desktopPane.setBorder(null);
        desktopPane.setBackground(Color.WHITE);
        contentPane.add(desktopPane, BorderLayout.CENTER);
        desktopPane.setLayout(new FormLayout(new ColumnSpec[] {
                FormSpecs.RELATED_GAP_COLSPEC,
                ColumnSpec.decode("default:grow"),},
                new RowSpec[] {
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("default:grow"),}));

        JSplitPane splitPane = new JSplitPane();
        splitPane.setForeground(B2BUtil.mainColor);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(0);
        splitPane.setOneTouchExpandable(false);
        splitPane.setBackground(Color.WHITE);
        desktopPane.add(splitPane, "2, 2, fill, fill");

        menu = new JPanel();
        //menu.setBorder(null);
        splitPane.setLeftComponent(menu);
        GridBagLayout gbl_Menu = new GridBagLayout();
        gbl_Menu.columnWidths = new int[] {150, 0};
        gbl_Menu.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
        gbl_Menu.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_Menu.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
        menu.setLayout(gbl_Menu);


        content = new JPanel();
        content.setBackground(Color.WHITE);
        //content.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
        splitPane.setRightComponent(content);
        content.setLayout(new CardLayout(0, 0));

        GridBagLayout gbl = new GridBagLayout();
        gbl.columnWidths = new int[] { 1, 1, 1, 1, 1, 1 };
        gbl.rowHeights = new int[]{ 1, 1, 1 };
        gbl.columnWeights = new double[]{ 0.02, 0.1, 0.38, 0.1, 0.38, 0.02 };
        gbl.rowWeights = new double[]{ 0.2, 0.6, 0.2 };

        // Creat panel and add it to the parent panel !!
        JPanel footerPanel = new JPanel(gbl);
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.add(footerPanel, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        gbc.gridy = 1;
        JLabel labelSynchronizingBlocks = new JLabel("Synchronizing blocks :");
        footerPanel.add(labelSynchronizingBlocks, gbc);

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 2;
        gbc.gridy = 1;
        dataSynchronizingBlocks = new JLabel("Loading ...");
        footerPanel.add(dataSynchronizingBlocks, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridx = 3;
        gbc.gridy = 1;
        JLabel labelSynchronizingNetwork = new JLabel("Synchronizing network :");
        footerPanel.add(labelSynchronizingNetwork, gbc);

        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 4;
        gbc.gridy = 1;
        footerPanel.add(progressBar, gbc);

        this.pack();
    }

    protected void setContainers(List<TabContainer> containers) {
        menus = new ArrayList<>();
        for (final TabContainer container : containers) {
            JComponent card = container.getView();
            card.setName(container.getName());
            content.add(card, container.getName());

            final JButton button = createMenuButton(menu, container.getIndex(), container.getName(), container.getIcon(), container.isEnabled());
            menus.add(button);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    CardLayout cl = (CardLayout) (content.getLayout());
                    cl.show(content, button.getText());

                    // TODO 3 - change text color to white and black vice versa !!!!!

                    for (JButton buttonLoop : menus) {
                        buttonLoop.setBackground(B2BUtil.mainColor);
                    }

                    for (JButton buttonLoop : menus) {
                        if (buttonLoop.getText().equals(button.getText())) {
                            buttonLoop.setBackground(B2BUtil.selectedColor);
                        }
                    }
                }
            });
        }
    }

    public void update(Observable rpcPoller, Object data) {
        if (data instanceof Status) {
            canStopWalletIfNotSyncedYet = true;
        }
        if (data instanceof BlockCount) {
            BlockCount blockCount = (BlockCount) data;
            setProgress((int)blockCount.getCount());
            dataSynchronizingBlocks.setText("" + progressBar.getValue() + " / " + progressBar.getMaximum());
            if ((progressBar.getValue() == progressBar.getMaximum()
                    && progressBar.getMaximum() != 0
                    && progressBar.getMaximum() != 100) || actionController.isOldDaemonSynced(progressBar.getValue())) {
                if (actionController.isOldDaemonSynced(progressBar.getValue())) {
                    try {
                        actionController.stopWalletd();
                        actionController.startOldWallet();
                        Thread.sleep(30000);
                        actionController.resetWallet();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    actionController.startWallet();
                }
            }
        }
        if (data instanceof com.b2beyond.wallet.rpc.model.Error) {
            System.out.println(((Error) data).getCode());
        }
        if (data instanceof ChangeSize) {
            // TODO -- this shit is not working at runtime, figure this out !!
            ChangeSize newsize = (ChangeSize) data;
            Dimension minimumSize = new Dimension(newsize.getWidth(), newsize.getHeight());
            this.setMinimumSize(minimumSize);
            this.setResizable(true);
            this.setSize(minimumSize);
            this.content.setMinimumSize(minimumSize);
            this.content.setSize(minimumSize);
            this.repaint();
        }
        if (data instanceof BlockHeaderWrapper) {
            dataSynchronizingBlocks.setText("" + progressBar.getValue() + " / " + progressBar.getMaximum());
        }
        if (data instanceof Error) {
//            if (canStopWalletIfNotSyncedYet) {
//                actionController.stopWalletd();
//                canStopWalletIfNotSyncedYet = false;
//            }
        }

        if (data instanceof String) {
            setProgressMax(Integer.parseInt((String)data));
        }
    }

    public void setProgressMax(long maxProgress) {
        final long theProgress = maxProgress;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                progressBar.setMaximum((int) theProgress);
            }
        });
        progressBar.setStringPainted(true);
    }

    public void setProgress(int progress) {
        final int theProgress = progress;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue(theProgress);
            }
        });
    }

    private JButton createMenuButton(JPanel menu, int yPosition, String buttonCaption, ImageIcon icon, boolean enabled) {
        JPanel buttonShowOverviewPanel = new JPanel();
        buttonShowOverviewPanel.setBackground(B2BUtil.mainColor);
        buttonShowOverviewPanel.setOpaque(true);
        //buttonShowOverviewPanel.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        GridBagConstraints gbc_buttonShowOverviewPanel = new GridBagConstraints();
        gbc_buttonShowOverviewPanel.fill = GridBagConstraints.BOTH;
        gbc_buttonShowOverviewPanel.gridx = 0;
        gbc_buttonShowOverviewPanel.gridy = yPosition;
        menu.add(buttonShowOverviewPanel, gbc_buttonShowOverviewPanel);
        GridBagLayout gbl_buttonShowOverviewPanel = new GridBagLayout();
        gbl_buttonShowOverviewPanel.columnWidths = new int[]{0, 0};
        gbl_buttonShowOverviewPanel.rowHeights = new int[]{0, 0};
        gbl_buttonShowOverviewPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_buttonShowOverviewPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        buttonShowOverviewPanel.setLayout(gbl_buttonShowOverviewPanel);

        final JButton buttonShowOverview = new JButton(buttonCaption);
        buttonShowOverview.setIcon(icon);
        buttonShowOverview.setBackground(B2BUtil.mainColor);
        //buttonShowOverview.setBorder(null);
        buttonShowOverview.setOpaque(true);
        buttonShowOverview.setEnabled(enabled);

        GridBagConstraints gbc_buttonShowOverview = new GridBagConstraints();
        gbc_buttonShowOverview.fill = GridBagConstraints.BOTH;
        gbc_buttonShowOverview.gridx = 0;
        gbc_buttonShowOverview.gridy = 0;
        buttonShowOverviewPanel.add(buttonShowOverview, gbc_buttonShowOverview);

        return buttonShowOverview;
    }
}

