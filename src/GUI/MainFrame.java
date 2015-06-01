package GUI;

 
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.sound.sampled.*;

import Controller.EV3PSController;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
* メインフレーム
* @author usui
*/
public class MainFrame extends JFrame{
    public String[] PanelNames = {"main","setting","play","result"};
    TitlePanel titlepanel = new TitlePanel(this,PanelNames[0]);
    SettingPanel settingpanel = new SettingPanel(this,PanelNames[1]);
    PlayingPanel playingpanel = new PlayingPanel(this,PanelNames[2]);
    ResultPanel resultpanel = new ResultPanel(this,PanelNames[3]);
    SoccerManager soccer;
   
    public  EV3PSController ev3controller = null;
	

    public MainFrame(){
        this.add(settingpanel);settingpanel.setVisible(false);
        this.add(playingpanel);playingpanel.setVisible(false);
        this.add(resultpanel);resultpanel.setVisible(false);
        this.add(titlepanel);titlepanel.setVisible(true);
        this.setBounds(0, 0, 1300, 700);
        
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        
        // EV3 の接続
        ev3controller = new EV3PSController();
		  
		  WindowListener listener = new WindowAdapter() {
				public void windowClosing(WindowEvent w) {
					if ( ev3controller != null)
						ev3controller.destruct();
					
					System.exit(0);
				}
			};
			this.addWindowListener(listener);
    }

    public static void main(String[] args) {
        MainFrame mainframe = new MainFrame();
        
    }

    /**
    *画面遷移のメソッド
    *@param jp 現在のパネル
    *@param str 現在のパネル名
    */
    public void PanelChange(JPanel jp, String str){
        String name = jp.getName();
        if(name == PanelNames[0]){
            titlepanel = (TitlePanel)jp;
            titlepanel.setVisible(false);
        }
        if(name == PanelNames[1]){
            settingpanel = (SettingPanel)jp;
            settingpanel.setVisible(false);
        }
        if(name == PanelNames[2]){
            playingpanel = (PlayingPanel)jp; 
            playingpanel.setVisible(false);
        }
        if(name == PanelNames[3]){
            resultpanel = (ResultPanel)jp; 
            resultpanel.setVisible(false);
         }

        
        
        
        if(str == PanelNames[0]){
            titlepanel.setVisible(true);
            this.add(titlepanel);
        }
        if(str == PanelNames[1]){
            settingpanel.setVisible(true);
            this.add(settingpanel);
        }
        if(str == PanelNames[2]){
            playingpanel.setVisible(true);
            this.add(playingpanel);
            
            this.ev3controller.kakerun.soccer_start();
            this.ev3controller.kunokuno.soccer_start();
        }
        if(str == PanelNames[3]){
        	this.ev3controller.kakerun.soccer_finish();
        	this.ev3controller.kunokuno.soccer_finish();
        	this.soccer.writeLog();
        	
            resultpanel.setVisible(true);
            this.add(resultpanel);
        }
    }

    /**
    *プレイヤーを登録するメソッド
    *@param name1 player1の名前
    *@param name2 player2の名前
    */
    public void RegistPlayer(String name1,String name2){
        soccer = new SoccerManager(new Player(name1),new Player(name2));
    }
}

/**
* タイトル画面
* @author usui
*/
 class TitlePanel extends JPanel {
    JPanel panel;
    JButton btn;
    JLabel label;
    MainFrame mainframe;
    String str;

    public TitlePanel(MainFrame m,String s){
        mainframe = m;
        str = s;
        this.setName("main");
    
        label = new JLabel("サッカーゲーム");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(new Font("MS　ゴシック",Font.BOLD,50));

        btn = new JButton("スタート");
        btn.setPreferredSize(new Dimension(200,80));
        btn.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        btn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                panelchange(mainframe.PanelNames[1]);
            }
        });

        panel = new JPanel();
        panel.add(btn);
        panel.setBackground(Color.GREEN);
        this.setBackground(Color.GREEN);
        this.add(label);
        this.add(panel);
        this.setLayout(new GridLayout(2,1));
    }

    /**
    *画面遷移のメソッド
    *@param str 現在のパネル名
    */
    public void panelchange(String str){
        mainframe.PanelChange((JPanel)this, str);
    }
}

/**
* ゲーム設定画面
* @author usui
*/
 class SettingPanel extends JPanel {
    JPanel panel,panel2,panel3,panel4,panel5,panel6;
    JButton btn,btn2;
    JLabel label,label2,label3,label4;
    JTextField text,text2;
    JRadioButton radio,radio2,radio3;
    ButtonGroup group;
    MainFrame mainframe;
    String str;

    public SettingPanel(MainFrame m,String s){
        mainframe = m;
        str = s;
        this.setName(s);
        this.setBackground(Color.GREEN);

        label = new JLabel("名前");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(new Font("MS　ゴシック",Font.BOLD,40));
        this.add(label);

        label2 = new JLabel("player1 : ");
        label2.setHorizontalAlignment(JLabel.CENTER);
        label2.setFont(new Font("MS　ゴシック",Font.BOLD,40));

        text = new JTextField();
        text.setFont(new Font("MS　ゴシック",Font.BOLD,30));

        panel = new JPanel();
        panel.add(label2);
        panel.add(text);
        panel.setLayout(new GridLayout(1,2));
        panel.setBackground(Color.GREEN);
        this.add(panel);

        label3 = new JLabel("player2 : ");
        label3.setHorizontalAlignment(JLabel.CENTER);
        label3.setFont(new Font("MS　ゴシック",Font.BOLD,40));
        
        text2 = new JTextField();
        text2.setFont(new Font("MS　ゴシック",Font.BOLD,30));

        panel2 = new JPanel();
        panel2.add(label3);
        panel2.add(text2);
        panel2.setLayout(new GridLayout(1,2));
        panel2.setBackground(Color.GREEN);
        this.add(panel2);

        label4 = new JLabel("プレイ時間");
        label4.setHorizontalAlignment(JLabel.CENTER);
        label4.setFont(new Font("MS　ゴシック",Font.BOLD,40));
        this.add(label4);

        radio = new JRadioButton("３分");
        radio.setHorizontalAlignment(JLabel.RIGHT);
        radio.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        
        radio2 = new JRadioButton("５分");
        radio2.setHorizontalAlignment(JLabel.CENTER);
        radio2.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        
        radio3 = new JRadioButton("７分");
        radio3.setHorizontalAlignment(JLabel.LEFT);
        radio3.setFont(new Font("MS　ゴシック",Font.BOLD,30));

        group = new ButtonGroup();
        group.add(radio);
        group.add(radio2);
        group.add(radio3);

        panel3 = new JPanel();
        panel3.add(radio);
        panel3.add(radio2);
        panel3.add(radio3);
        panel3.setLayout(new GridLayout(1,3));
        panel3.setBackground(Color.GREEN);

        this.add(panel3);

        //キャンセルボタン
        btn = new JButton("キャンセル");
        btn.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        btn.setPreferredSize(new Dimension(200,80));
        btn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                mainframe.settingpanel.reset();
                panelchange(mainframe.PanelNames[0]);
            }
        });


        //決定ボタン
        btn2 = new JButton("決定");
        btn2.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        btn2.setPreferredSize(new Dimension(200,80));
        btn2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                if(text.getText().equals("") || text2.getText().equals("")){
                    //名前が入力されていないとき
                    JOptionPane.showMessageDialog(mainframe,"名前を入力してください");
                    
                }else if(!radio.isSelected() && !radio2.isSelected() && !radio3.isSelected()){
                    //プレイ時間が選択されていないとき
                    JOptionPane.showMessageDialog(mainframe,"プレイ時間を選択してください");
               
                }else{
                    //プレイヤーの登録
                    mainframe.RegistPlayer(text.getText(),text2.getText());

                    //プレイ時間の登録
                    if(radio.isSelected()){mainframe.soccer.setSelectTime(3);}
                    else if(radio2.isSelected()){mainframe.soccer.setSelectTime(5);}
                    else{mainframe.soccer.setSelectTime(7);}
                    
                    //プレイヤー情報反映
                    mainframe.playingpanel.update();
                    
                    panelchange(mainframe.PanelNames[2]);
               
                }
            }
        });

        panel4 = new JPanel();
        panel5 = new JPanel();
        panel6 = new JPanel();
        panel5.add(btn);
        panel6.add(btn2);
        panel4.add(panel5);
        panel4.add(panel6);
        panel4.setLayout(new GridLayout(1,2));
        panel5.setBackground(Color.GREEN);
        panel6.setBackground(Color.GREEN);
        this.add(panel4);

        this.setLayout(new GridLayout(6,1));
    }

    /**
    *履歴の削除メソッド
    */
    public void reset(){
        text.setText("");
        text2.setText("");
    }

    /**
    *画面遷移のメソッド
    *@param str 現在のパネル名
    */
    public void panelchange(String str){
        mainframe.PanelChange((JPanel)this, str);
    }
}

/**
* ゲーム中の画面
* @author usui
*/
class PlayingPanel extends JPanel implements ActionListener{
    JPanel panel,panel2,panel3,panel4,panel5,panel6;
    JButton btn,btn2,btn3,btn4;
    JLabel label,label2,label3,label4,label5;
    MainFrame mainframe;
    String str;
    int second = 0,minute = 0;
    Timer timer;
    boolean pause = false;

    public PlayingPanel(MainFrame m,String s){
        mainframe = m;
        str = s;
        this.setName(s);
        this.setBackground(Color.GREEN);
        
        

        label = new JLabel();
        label.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        label.setHorizontalAlignment(JLabel.CENTER);
        label2 = new JLabel();
        label2.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        label2.setHorizontalAlignment(JLabel.CENTER);

        panel = new JPanel();
        panel.add(label);
        panel.add(label2);
        panel.setLayout(new GridLayout(1,2));
        panel.setBackground(Color.GREEN);
        this.add(panel);

        label3 = new JLabel();
        label3.setFont(new Font("MS　ゴシック",Font.BOLD,60));
        label3.setHorizontalAlignment(JLabel.CENTER);
        label4 = new JLabel();
        label4.setFont(new Font("MS　ゴシック",Font.BOLD,60));
        label4.setHorizontalAlignment(JLabel.CENTER);

        panel2 = new JPanel();
        panel2.add(label3);
        panel2.add(label4);
        panel2.setLayout(new GridLayout(1,2));
        panel2.setBackground(Color.GREEN);
        this.add(panel2);

        btn = new JButton("ゴール");
        btn.setFont(new Font("MS　ゴシック",Font.BOLD,30)); 
        btn.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                int mouseButton = e.getButton();
                if(mouseButton == MouseEvent.BUTTON1){
                    mainframe.soccer.player1.addPoint();
                    SoundGenerate("res/whistle.wav");
                }else{
                    mainframe.soccer.player1.deletePoint();
                }
            label3.setText(String.valueOf(mainframe.soccer.player1.point));
            }
        });

        btn2 = new JButton("ゴール");
        btn2.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        btn2.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                int mouseButton = e.getButton();
                if(mouseButton == MouseEvent.BUTTON1){
                    mainframe.soccer.player2.addPoint();
                    SoundGenerate("res/whistle.wav");
                }else{
                    mainframe.soccer.player2.deletePoint();
                }
            label4.setText(String.valueOf(mainframe.soccer.player2.point));
            }
        });

        panel3 = new JPanel();
        panel4 = new JPanel();
        panel5 = new JPanel();
        panel4.add(btn);
        panel5.add(btn2);
        panel3.add(panel4);
        panel3.add(panel5);
        panel3.setLayout(new GridLayout(1,2));
        panel3.setBackground(Color.GREEN);
        panel4.setBackground(Color.GREEN);
        panel5.setBackground(Color.GREEN);
        this.add(panel3);


        label5 = new JLabel("経過時間");
        label5.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        this.add(label5);

        btn3 = new JButton("中断");
        btn3.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        btn3.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                SoundGenerate("res/game_end.wav");
                mainframe.resultpanel.update();
                timer.stop();

                panelchange(mainframe.PanelNames[3]);
            }
        });
        btn3.setPreferredSize(new Dimension(200,80));
        
        btn4 = new JButton("一時停止");
        btn4.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        btn4.addActionListener(new ActionListener(){
           
			public void actionPerformed(ActionEvent e){
              if (pause == false) {
            	  mainframe.ev3controller.kakerun.soccer_finish();
            	  mainframe.ev3controller.kunokuno.soccer_finish();
            	  btn4.setText("再開");
            	  timer.stop();
            	  
              }
              else {
            	  mainframe.ev3controller.kakerun.soccer_start();
            	  mainframe.ev3controller.kunokuno.soccer_start();
            	  btn4.setText("一時停止");
            	  timer.start();
              }
              pause = !pause;
               
               System.out.println(pause);
                
            }
        });
        btn4.setPreferredSize(new Dimension(200,80));
        panel6 = new JPanel();
        panel6.add(btn3);
        panel6.add(btn4);
        panel6.setBackground(Color.GREEN);
        this.add(panel6);


        this.setLayout(new GridLayout(5,1));
    }

    /**
    *画面の更新、タイマーをスタートさせるメソッド
    */
    public void update(){
        label.setText(mainframe.soccer.player1.name);
        label2.setText(mainframe.soccer.player2.name);
        label3.setText(String.valueOf(mainframe.soccer.player1.point));
        label4.setText(String.valueOf(mainframe.soccer.player2.point));
        timer = new Timer(1000,this);
        timer.start();
    }

    /**
    *履歴削除のメソッド
    */
    public void reset(){
        label.setText("");
        label2.setText("");
        label3.setText("");
        label4.setText("");
        label5.setText("");
        second = 0;
        minute = 0;
    }

    /**
    *ファイルを読み込み再生する
    *@param filePath ファイルパス
    */
    public void SoundGenerate(String filePath){
        AudioFormat format = null;
        DataLine.Info info = null;
        Clip line = null;
        File audioFile = null;
        
        String current = "./";
        File currentdir = new File(current);
        System.out.println("----------files---------");
        File[]  files = currentdir.listFiles();
        for (File file : files){
        	System.out.println(file);
        }
        
        System.out.println("---files----");

        try{
            audioFile = new File(filePath);
            format = AudioSystem.getAudioFileFormat(audioFile).getFormat();
            info = new DataLine.Info(Clip.class,format);
            line = (Clip)AudioSystem.getLine(info);
            line.open(AudioSystem.getAudioInputStream(audioFile));
            line.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
    *画面遷移のメソッド
    *@param str 現在のパネル名
    */
    public void panelchange(String str){
        mainframe.PanelChange((JPanel)this, str);
    }

    /**
    *プレイ時間を表示するメソッド
    *@param e ActionEvent
    */
    public void actionPerformed(ActionEvent e){//1秒ごとに呼ばれる
        second++;
        if(second == 60){
            second = 0;
            minute++;
        }

        label5.setText("経過時間   "+String.format("%1$02d",minute)+" : "+String.format("%1$02d",second));
        if(minute == mainframe.soccer.selectTime){//プレイ時間終了で画面移動
            timer.stop();
            SoundGenerate("res/game_end.wav");
            mainframe.resultpanel.update();
            panelchange(mainframe.PanelNames[3]);
        }
    }
}

/**
* 結果表示画面
* @author usui
*/
class ResultPanel extends JPanel {
    JPanel panel;
    JButton btn;
    JLabel label,label2;
    MainFrame mainframe;
    String str;

    public ResultPanel(MainFrame m,String s){
        mainframe = m;
        str = s;
        this.setName(s);
        this.setBackground(Color.GREEN);

        label = new JLabel("結果発表");
        label.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        label.setHorizontalAlignment(JLabel.CENTER);
        this.add(label);

        label2 = new JLabel();
        label2.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        label2.setHorizontalAlignment(JLabel.CENTER);
        this.add(label2);

        btn = new JButton("終了");
        btn.setFont(new Font("MS　ゴシック",Font.BOLD,30));
        btn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                mainframe.settingpanel.reset();
                mainframe.playingpanel.reset();
                mainframe.resultpanel.reset();
                panelchange(mainframe.PanelNames[0]);
            }
        });
        btn.setPreferredSize(new Dimension(200,80));
        panel = new JPanel();
        panel.add(btn);
        panel.setBackground(Color.GREEN);
        this.add(panel);

        this.setLayout(new GridLayout(3,1));
    }

    /**
    *画面を更新するメソッド
    */
    public void update(){
        label2.setText(mainframe.soccer.jadge());
    }

    /**
    *履歴を削除するメソッド
    */
    public void reset(){
        label2.setText("");
    }

    /**
    *画面遷移のメソッド
    *@param str 現在のパネル名
    */
    public void panelchange(String str){
        mainframe.PanelChange((JPanel)this, str);
    }
}

/**
* プレイヤー情報（名前と点数）
* @author usui
*/
class Player{
    String name;
    int point;

    public Player(String name){
        this.name = name;
        this.point = 0;
    }

    /**
    *ポイントを追加するメソッド
    */
    public void addPoint(){
        if(this.point < 100){
            this.point++;
        }
    }

    /**
    *ポイントを減らすメソッド
    */
    public void deletePoint(){
        if(this.point > 0){
            this.point--;
        }
    }
}

/**
* プレイ時間の登録と勝利判定
* @author usui
*/
class SoccerManager{
    Player player1,player2;
    int selectTime;

    public SoccerManager(Player player1,Player player2){
        this.player1 = player1;
        this.player2 = player2;
        this.selectTime = 0;
    }

    /**
    *プレイ時間を登録するメソッド
    *@param time 選択した時間
    */
    public void setSelectTime(int time){
        this.selectTime = time;
    }

    /**
    *勝利判定のメソッド
    *@return 勝利判定を出力
    */
    public String jadge(){
        if(player1.point > player2.point){
            return (player1.point+" 対 "+player2.point+"で "+player1.name+"さんの勝ち！！");
        }else if(player1.point < player2.point){
            return (player1.point+" 対 "+player2.point+"で "+player2.name+"さんの勝ち！！");
        }else return (player1.point+" 対 "+player2.point+"で 引き分け");
    }
    
    public void writeLog(){
    	try{
    		//現在日時を取得する
            Calendar c = Calendar.getInstance();

            //フォーマットパターンを指定して表示する
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd H:mm E");
            System.out.println(sdf.format(c.getTime()));

            //クラスのインスタンス化、引数でファイル名を指定
            FileWriter fw = new FileWriter("res/logfile.xml", true);
           fw.write("\r<play_data" + "\r");
           fw.write("date=\"" + sdf.format(c.getTime()) + "\">\r");
           
           fw.write("<player" + "\r");
           fw.write("name=\"" + player1.name + "\"\r");
           fw.write("point=\"" + player1.point + "\"\r");
           fw.write("/>" + "\r");
           
           fw.write("<player" + "\r");
           fw.write("name=\"" + player2.name + "\"\r");
           fw.write("point=\"" + player2.point + "\"\r");
           fw.write("/>" + "\r");
           
           fw.write("</playdata>\r");
           
            fw.close();
       } catch(Exception e){
    }
   }
}
    