package leeapp.company;

import java.io.File;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import leeapp.company.task.InviteTask;
import leeapp.company.task.PayTask;
import me.onebone.economyapi.EconomyAPI;

public class LeeAppCompany extends PluginBase implements Listener{
	
	private Company com = new Company();
	private Config company;
	public HashMap<String, String> invite = new HashMap<>();
	private static String BASE = "[ LA_Company ] ";
	
	public String getCompany(Player user){
		return com.getUserCompany(user, company);
	}
	
	public String[] getCompanyList(){
		return com.getCompanyList(company);
	}
	
	public boolean isOwner(Player user){
		return com.isOwner(user, company);
	}
	
	public boolean isSubOwner(Player user){
		return com.isSubOwner(user, company);
	}
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getLogger().info("LeeAppcompany onEnable");
		getDataFolder().mkdirs();
		company = new Config(new File(getDataFolder(), "company.yml"), Config.YAML);
		company.save();
		if(!company.getAll().containsKey("createMoney")){
			company.set("createMoney", 100000);
			company.save();
		}
		if(!company.getAll().containsKey("time")){
			company.set("time", 120);
			company.save();
		}
		getServer().getScheduler().scheduleRepeatingTask(new PayTask(this, company), (company.getInt("time") * 20 * 60));
	}
	
	public long getNumber(String s){
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			return -1;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(command.getName().equals("ȸ��")){
			if(args.length == 0){
				sender.sendMessage("/ȸ�� ���� < ȸ���̸� >");
				sender.sendMessage("/ȸ�� ���� < ȸ���̸� > ( *ȸ�� ������� 10���� )");
				sender.sendMessage("/ȸ�� �ڱ� �ݾ�");
				sender.sendMessage("/ȸ�� ����Ʈ");
				return false;
			}
			switch(args[0]){
			case "�ڱ�":
				if(args.length == 1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ�翡 �ڱ����� ������ �ݾ��� �Է��ϼ���!");
					return false;
				}
				if(getNumber(args[1]) == -1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�˸��� ���� ������ �ƴմϴ�!");
					return false;
				}
				if(EconomyAPI.getInstance().myMoney(sender.getName()) < getNumber(args[1])){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�����ݾ��� �����մϴ�!");
					return false;
				}
				com.addFund(getNumber(args[1]), com.getUserCompany((Player)sender, company), company);
				EconomyAPI.getInstance().reduceMoney(sender.getName(), getNumber(args[1]));
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.AQUA + "ȸ�翡 �ڱ��� " + TextFormat.WHITE + getNumber(args[1]) + TextFormat.AQUA + "��ŭ �־����ϴ�.");
				return true;
			case "����":
				if(args.length == 1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "������ Ȯ���� ȸ����� �Է��� �ּ���!");
					return false;
				}
				if(!com.containCompany(company, args[1])){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�ǹٸ� ȸ����� �Է��� �ּ���!");
					return false;
				}
				String list = "";
				for(String li : com.getList(args[1], company)){
					list += li + ", ";
				}
				sender.sendMessage(args[1] + "ȸ��\n- ȸ��: " + com.getOwner(args[1], company) + "\n- ��ȸ��: " + com.getSubOwner(args[1], company) + "\n- �ڱ�: " + "\n- ���: " + list);
				break;
			case "����":
				
				if(args.length == 1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "������ ȸ�� �̸��� �����ּ���!");
					return false;
				}
				if(!com.getUserCompany((Player)sender, company).equals("���Ҽ�")){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�̹� ȸ�翡 �ҼӵǾ� �ֽ��ϴ�!");
					return false;
				}
				if(args[1].equals("���Ҽ�")){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�� ȸ���̸��� ��� �ϽǼ� �����ϴ�!");
					return false;
				}
				if(com.isCompanyBeing(args[1], company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�̹� �����ϴ� ȸ��� �Դϴ�!");
					return false;
				}
				if(EconomyAPI.getInstance().myMoney(sender.getName().toLowerCase()) < com.getCreateMoney(company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ�縦 �����ϴµ� �ʿ��� ���� �����մϴ�!");
					return false;
				}
				EconomyAPI.getInstance().reduceMoney(sender.getName().toLowerCase(), com.getCreateMoney(company));
				com.createCompany(company, args[1], sender);
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.AQUA + args[1] + "ȸ�縦 ���������� �����Ͼ����ϴ�!");
				break;
			case "����Ʈ":
				String li = "";
				int i = 1;
				for(String list2 : com.getCompanyList(company)){
					li += i + ". " + list2 + " ";
					i++;
				}
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + "ȸ�� ����Ʈ: \n" + li);
				break;
			case "������":
				if(com.getUserCompany((Player)sender, company).equals("���Ҽ�")){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ�翡 �ҼӵǾ� ���� �ʽ��ϴ�!");
					return false;
				}
				String name = com.getUserCompany((Player)sender, company);
				com.delList(com.getUserCompany((Player)sender, company), (Player)sender, company);
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + name + "ȸ�翡�� �����̽��ϴ�.");
				break;
			case "����":
				if(invite.containsKey(sender.getName().toLowerCase())){
					com.addList(invite.get(sender.getName().toLowerCase()), (Player)sender, company);
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + invite.get(sender.getName().toLowerCase()) + "ȸ�翡 �Ի��ϼ̽��ϴ�.");
					invite.remove(invite.get(sender.getName().toLowerCase()));
					return true;
				}
				break;
			case "����":
				if(invite.containsKey(sender.getName().toLowerCase())){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + invite.get(sender.getName().toLowerCase()) + "ȸ�翡 �ʴ븦 �����ϼ̽��ϴ�.");
					invite.remove(invite.get(sender.getName().toLowerCase()));
					return true;
				}
				break;
			default:
				sender.sendMessage("/ȸ�� ���� < ȸ���̸� >");
				sender.sendMessage("/ȸ�� ���� < ȸ���̸� > ( *ȸ�� ������� 10���� )");
				sender.sendMessage("/ȸ�� ����Ʈ");
			}
		}
		if(command.getName().equals("ȸ�����")){
			if(!(com.isOwner((Player)sender, company) || com.isSubOwner((Player)sender, company))){
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ��/��ȸ�常 ���� �ִ� ��ɾ� �Դϴ�!");
				return false;
			}
			switch(args[0]){
			case "�޿�":
				if(com.isSubOwner((Player)sender, company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ�常 ���� �ִ� ��ɾ� �Դϴ�!");
					return false;
				}
				if(args.length == 1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ��޿��� �Է��ϼ���!");
					return false;
				}
				if(getNumber(args[1]) == -1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�˸��� ���� ������ �ƴմϴ�!");
					return false;
				}
				com.setAmount(com.getUserCompany((Player)sender, company), getNumber(args[1]), company);
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.AQUA + "ȸ��޿��� " + TextFormat.WHITE + args[1] + TextFormat.AQUA + "�� ���� �ϼ̽��ϴ�.");
				break;
			case "�ڱ�":
				if(com.isSubOwner((Player)sender, company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ�常 ���� �ִ� ��ɾ� �Դϴ�!");
					return false;
				}
				if(args.length == 1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ���ڱݿ��� ���� �ݾ��� �Է��ϼ���!");
					return false;
				}
				if(getNumber(args[1]) == -1){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�˸��� ���� ������ �ƴմϴ�!");
					return false;
				}
				if(com.getFund(com.getUserCompany((Player)sender, company), company) < getNumber(args[1])){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ�� �ڱ��� �����մϴ�!");
					return false;
				}
				com.reduceFund(getNumber(args[1]), com.getUserCompany((Player)sender, company), company);
				EconomyAPI.getInstance().addMoney((Player)sender, getNumber(args[1]));
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.AQUA + "ȸ�翡 �ڱ��� " + TextFormat.WHITE + getNumber(args[1]) + TextFormat.AQUA + "��ŭ �����ϴ�.");
				break;
			case "��ȸ��":
				if(com.isSubOwner((Player)sender, company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ�常 ���� �ִ� ��ɾ� �Դϴ�!");
					return false;
				}
				Player user = getServer().getPlayer(args[1]);
				if(user == null){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�������� �ʴ� ���� �Դϴ�!");
					return false;
				}
				if(!com.getUserCompany((Player)sender, company).equals(com.getUserCompany(user, company))){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�ش� ������ ����� ȸ�� �Ҽ��� �ƴմϴ�!");
					return false;
				}
				com.setSubOwner(com.getUserCompany((Player)sender, company), user, company);
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + user.getName() + "�� ��ȸ������ �����ϼ̽��ϴ�!");
				break;
			case "����":
				if(com.isSubOwner((Player)sender, company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ�常 ���� �ִ� ��ɾ� �Դϴ�!");
					return false;
				}
				getServer().getLogger().info(TextFormat.DARK_BLUE + BASE + TextFormat.RED + com.getUserCompany((Player)sender, company) + "ȸ�簡 �����Ǿ����ϴ�!");
				com.removeCompany(company, com.getUserCompany((Player)sender, company));
				break;
			case "�ʴ�":
				Player user2 = getServer().getPlayer(args[1]);
				if(user2 == null){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�������� �ʴ� ���� �Դϴ�!");
					return false;
				}
				invite.put(user2.getName().toLowerCase(), com.getUserCompany((Player)sender, company));
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + user2.getName() + "���� ȸ�翡 �ʴ��ϼ̽��ϴ�.");
				user2.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GREEN + com.getUserCompany((Player)sender, company) + "ȸ�翡�� �ʴ� �ϼ̽��ϴ�. < /ȸ�� ����or���� >");
				getServer().getScheduler().scheduleDelayedTask(new InviteTask(this, user2), 200);
				break;
			case "����":
				Player user3 = getServer().getPlayer(args[1]);
				if(user3 == null){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�������� �ʴ� ���� �Դϴ�!");
					return false;
				}
				if(!com.getUserCompany(user3, company).equals(com.getUserCompany((Player)sender, company))){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "�ش� ������ ����� ȸ��Ҽ��� �ƴմϴ�!");
					return false;
				}
				if(com.isOwner(user3, company)){
					sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.RED + "ȸ���� �����ų �� �����ϴ�!");
					return false;
				}
				com.delList(com.getUserCompany((Player)sender, company), user3, company);
				sender.sendMessage(TextFormat.DARK_BLUE + BASE + TextFormat.GOLD + user3.getName() + "���� ������ ���� ��Ű�̽��ϴ�.");
				break;
			default:
				sender.sendMessage("/ȸ����� ��ȸ�� < �÷��̾� > ( *ȸ�常 ��밡��)");
				sender.sendMessage("/ȸ����� �ڱ� �ݾ�");
				sender.sendMessage("/ȸ����� ���� ( *ȸ�常 ��밡��)");
				sender.sendMessage("/ȸ����� �ʴ� < �÷��̾� >");
				sender.sendMessage("/ȸ����� ���� < �÷��̾� >");
			}
		}
		return true;
	}
}
