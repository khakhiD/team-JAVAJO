package Main;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main
{
	static Lecture [] getDatabase(String type, String userMajor, int userGrade, int userSemester)
	{
		int i = 0;
		Lecture [] lec = null;
		Connection conn = null;
		
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
        	
			String url = "jdbc:mysql://localhost/test?characterEncoding=UTF-8&serverTimezone=UTC";
			//jdbc:mysql://~~~/디비이름, 물음표부터는 타임존 에러 방지용 문구. 만지면 큰일남.
        	
			conn = DriverManager.getConnection(url, "root", "1q2w3e4r!");
			//(, "이름", "비밀번호")
			System.out.println("DB 연결 성공");
        	
			// 전공 필수 불러오기
			
			String query = "";
			if(type == "전공필수")
			{
				query = "select * from " + userMajor + "_" + userGrade + "학년_" + userSemester + "학기_전공필수";
			}
			else if(type == "전공선택")
			{
				query = "select * from " + userMajor + "_" + userGrade + "학년_" + userSemester + "학기_전공선택";
			}
			else if(type == "균형교양")
			{
				query = "select * from " + "균형_" + "1학년_공통";
			}
			
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			// 테이블 행의 길이
			int tableSize = 0;
			while(rs.next())
			{
				tableSize++;
			}
			
			// 전공 필수 목록 객체 생성
			lec = new Lecture[tableSize];	// 전공 필수 강의 DB
			
			rs = st.executeQuery(query);
			while(rs.next())
			{
				int id = rs.getInt("id");
				int grade = rs.getInt("grade");
				String name = rs.getString("classname");
				int credit = rs.getInt("score");
				int number_of_people = rs.getInt("personnumber");
				String professor = rs.getString("professor");
				String building = rs.getString("place");
				int classroom_1 = rs.getInt("cn1");
				int start_1 = rs.getInt("start1");
				int end_1 = rs.getInt("end1");
				int classroom_2 = rs.getInt("cn2");
				int start_2 = rs.getInt("start2");
				int end_2 = rs.getInt("end2");
				
				lec[i++] = new Lecture(name, credit, number_of_people, professor, building,
					classroom_1, start_1, end_1, classroom_2, start_2, end_2);
			}
        	
        	st.close();
        }
        catch(Exception e)
        {
        	System.err.println("DB 연결 실패");
			System.err.println(e.getMessage());
		}
		
		return lec;
	}
	
	// 재귀 함수와 조합 알고리즘을 통해 모든 시간표의 경우의 수 산출
	// n = 배열의 길이, r = 조합의 길이
	static void combination(Lecture[] lec, Schedule [] sche, boolean[] visited, int start, int n, int r, int combinationSize, int userCredit, String [] requiredName)
    {
        if (r == 0)
        {
            addLecture(lec, sche, visited, n, combinationSize, userCredit, requiredName);
            return;
        }

        for (int i = start; i < n; i++)
        {
            visited[i] = true;
            combination(lec, sche, visited, i + 1, n, r - 1, combinationSize, userCredit, requiredName);
            visited[i] = false;
        }
    }

    // 강의 추가
    static void addLecture(Lecture[] lec, Schedule [] sche, boolean[] visited, int n, int combinationSize, int userCredit, String [] requiredName)
    {
    	int i = 0, j = 0, k = 0;
    	
    	for (i = 0; i < n; i++)
        {
        	for(k = 0; k < sche[Schedule.num].size; k++)
        	{
        		// 시간표 안에 이름이 같은 강의(== 분반이 다른 같은 강의)가 있을 경우 시간표를 작성하지 않음
        		if(sche[Schedule.num].lectures[k].name == lec[i].name)
				{
					visited[i] = false;
				}
        		else	// 시간표에 이미 추가된 강의와 강의 시간이 겹치지 않는지 여부 판단
        		{
        			int x = 0;
        			int y = 0;
        			
        			int[] term_1 = new int[6];	// 시간표에 이미 추가된 강의의 강의 시간
					int[] term_2 = new int[6];	// 시간표에 추가 가능 여부를 판단하는 강의의 강의 시간
					
					for(x = sche[Schedule.num].lectures[k].start_1; x <= sche[Schedule.num].lectures[k].end_1; x++)
					{
						term_1[y++] = x;
					}
					
					// classroom_2 == 0일 경우 한 주에 두 번째 강의가 없음
					if(sche[Schedule.num].lectures[k].classroom_2 != 0)
					{
						for(x = sche[Schedule.num].lectures[k].start_2; x <= sche[Schedule.num].lectures[k].end_2; x++)
						{
							term_1[y++] = x;
						}
					}
					
					y = 0;
					for(x = lec[i].start_1; x <= lec[i].end_1; x++)
					{
						term_2[y++] = x;
					}
					
					if(lec[i].classroom_2 != 0)
					{
						for(x = lec[i].start_2; x <= lec[i].end_2; x++)
						{
							term_2[y++] = x;
						}
					}
					
					for(x = 0; x < term_1.length; x++)
					{
						for(y = 0; y < term_2.length; y++)
						{
							// term_1[x] == 0일 경우 해당 강의가 1시간, 또는 2시간 연강(== 3시간 연강이 아님)이기 때문에 그 시간이 겹치는지 확인할 필요가 없음
							if(term_1[x] == term_2[y] && term_1[x] != 0 && term_2[y] != 0)
							{
								visited[i] = false;
							}
						}
					}
        		}
			}
        	
        	if (visited[i])
            {
            	sche[Schedule.num].lectures[j] = lec[i];
            	sche[Schedule.num].size++;
            	j++;
            }
        }
    	
    	// 완성된 시간표의 강의 개수가 입력된 강의 개수와 같다면 시간표의 possibility 멤버에 true 저장
    	
    	int creditSum = 0;				// 시간표에 저장된 강의의 학점의 합
		boolean requiredCheck1 = true;	// 필수 강의가 전부 포함되었는지 체크
		boolean requiredCheck2 = false;
		
		for(i = 0; i < sche[Schedule.num].size; i++)
    	{
    		creditSum += sche[Schedule.num].lectures[i].credit;
    	}
    	
    	for(i = 0; i < sche[Schedule.num].lectures.length; i++)
		{
			if(requiredCheck1 == false)
			{
				break;
			}
				
			for(j = 0; j < requiredName.length; j++)
			{
				if(sche[Schedule.num].lectures[i].name.equals(requiredName[j]))
				{
					requiredCheck2 = true;
				}
			}
			
			if(requiredCheck2 == false)
			{
				requiredCheck1 = false;
			}
		}
    	
    	if(creditSum == userCredit && requiredCheck1 && sche[Schedule.num].size == combinationSize)
    	{
    		sche[Schedule.num].possibility = true;
    	}
    	
    	// 시간표 순번 증감
        Schedule.num++;
    }
	
	public static void main(String [] args)
	{
		int i = 0, j = 0, k = 0;
		
		String userMajor = "컴퓨터공학";	// 사용자의 학과
		int userGrade = 2;				// 사용자의 학년
		int userSemester = 2;			// 사용자의 학기
		int userCredit = 17;			// 사용자가 이수하고 하는 학점
		int userLecSize = 0;			// 분반을 포함한, 사용자가 신청하고자 하는 강의 목록의 개수
		
		String classCheck = "";			// 강의 이름을 통해 분반 여부를 확인할 변수
		String [] requiredName;			// 필수 강의의 이름
		int requiredCount = 0;			// 필수 강의 개수
		
		System.out.println("사용자가 " + userMajor + "과 " + userGrade + "학년 " + userSemester + "학기이며, 한 학기에 " + userCredit + "학점 이수를 원한다고 가정");
		System.out.println("사용자가 선택한 강의가 비주얼프로그래밍, 자료구조, 컴퓨터구조, 논리설계및실습, 자바프로그래밍, 현대자본주의이해, 기초수학이라 가정");
		System.out.println("");
		
		Lecture [] requiredLec;		// 전공 필수 강의 DB
		Lecture [] selectionLec;	// 전공 선택 강의 DB
		Lecture [] liberal; 		// 교양 강의 DB
		requiredLec = getDatabase("전공필수", userMajor, userGrade, userSemester);
		selectionLec = getDatabase("전공선택", userMajor, userGrade, userSemester);
		liberal = getDatabase("균형교양", userMajor, userGrade, userSemester);
		
		// 조합 알고리즘을 통해 작성된 모든 시간표를 저장할 배열
		Schedule [] all_schedules;
		all_schedules = new Schedule[100000];
		// 모든 조건을 충족시키는 시간표만 저장할 배열
		Schedule [] complete_schedules;
		complete_schedules = new Schedule[1000];
		
		// 시간표 저장 배열의 객체 생성
		for(i = 0; i < complete_schedules.length; i++)
		{
			complete_schedules[i] = new Schedule();
		}
		
		// 시연용 사용자 입력값 예시
		Lecture [] userLec;
		userLec = new Lecture[16];
		userLec[0] = new Lecture("비주얼프로그래밍", 3, 35, "안귀임", "정보", 814, 29, 30, 810, 37, 38);
		userLec[1] = new Lecture("비주얼프로그래밍", 3, 35, "안귀임", "정보", 816, 25, 26, 810, 37, 38);
		userLec[2] = new Lecture("비주얼프로그래밍", 3, 35, "최병윤", "정보", 814, 3, 3, 814, 17, 18);
		userLec[3] = new Lecture("자료구조", 3, 50, "김진일", "정보", 811, 5, 6, 812, 19, 19);
		userLec[4] = new Lecture("자료구조", 3, 50, "김진일", "정보", 812, 11, 12, 811, 27, 27);
		userLec[5] = new Lecture("컴퓨터구조", 3, 50, "이문노", "정보", 811, 17, 17, 812, 34, 35);
		userLec[6] = new Lecture("컴퓨터구조", 3, 50, "이문노", "정보", 812, 14, 15, 812, 27, 27);
		userLec[7] = new Lecture("논리설계및실습", 3, 50, "이문노", "정보", 812, 21, 22, 815, 29, 30);
		userLec[8] = new Lecture("논리설계및실습", 3, 50, "이문노", "정보", 812, 21, 22, 815, 31, 32);
		userLec[9] = new Lecture("논리설계및실습", 3, 50, "김화선", "정보", 815, 2, 3, 815, 14, 15);
		userLec[10] = new Lecture("자바프로그래밍", 3, 33, "장시웅", "정보", 811, 9, 10, 816, 29, 30);
		userLec[11] = new Lecture("자바프로그래밍", 3, 33, "장시웅", "정보", 811, 9, 10, 817, 31, 32);
		userLec[12] = new Lecture("자바프로그래밍", 3, 33, "장시웅", "정보", 817, 23, 24, 817, 34, 35);
		userLec[13] = new Lecture("현대자본주의이해", 2, 24, "박태진", "상경", 512, 7, 8, 0, 0, 0);
		userLec[14] = new Lecture("문학과인생", 2, 20, "김도희", "1인", 504, 14, 15, 0, 0, 0);
		userLec[15] = new Lecture("기초수학", 3, 46, "류재칠", "정보", 615, 29, 30, 615, 33, 33);
		
		classCheck = "";
		for(i = 0; i < requiredLec.length; i++)
		{
			if(!requiredLec[i].name.equals(classCheck))	// 강의 이름(classCheck)을 통해 분반 여부 검사
			{
				requiredCount++;						// 강의 개수  증감
				classCheck = requiredLec[i].name;		// 강의 이름(classCheck) 업데이트
			}
		}
		
		requiredName = new String[requiredCount];
		
		classCheck = "";
		j = 0;
		for(i = 0; i < requiredLec.length; i++)
		{
			if(!requiredLec[i].name.equals(classCheck))
			{
				requiredName[j++] = requiredLec[i].name;
				classCheck = requiredLec[i].name;
			}
		}
		
		userLecSize = userLec.length;
		boolean[] visited = null;
		
		for(int l = 1; l < 11; l++)
		{
			// 시간표 저장 배열의 객체 생성
			for(i = 0; i < all_schedules.length; i++)
			{
				all_schedules[i] = new Schedule();
			}
			
			visited = new boolean[userLecSize];	// 경우의 수를 모두 구할 재귀 함수에 사용할 부울 배열
			combination(userLec, all_schedules, visited, 0, userLecSize, l, l, userCredit, requiredName);
			
			// 모든 조건을 충족시키는 시간표만 저장
			for(i = 0; i < all_schedules.length; i++)
			{
				if(all_schedules[i].possibility)
				{	
					complete_schedules[k++].lectures = all_schedules[i].lectures;
				}
			}
		}
		
		System.out.println("");
		for(i = 0; i < k; i++)
		{
			System.out.println("시간표 " + (i + 1));
			for(j = 0; j < complete_schedules[0].lectures.length; j++)
			{
				if(complete_schedules[i].lectures[j].name != "")
				{
					System.out.print(complete_schedules[i].lectures[j].name + " " + complete_schedules[i].lectures[j].professor + " " + complete_schedules[i].lectures[j].classroom_1 + " ");
					System.out.print(complete_schedules[i].lectures[j].start_1 + " " + complete_schedules[i].lectures[j].end_1 + " " + complete_schedules[i].lectures[j].classroom_2 + " ");
					System.out.println(complete_schedules[i].lectures[j].start_2 + " " + complete_schedules[i].lectures[j].end_2);
				}
			}
			System.out.println("");
		}
		
		System.out.println("프로그램 종료");
	}
}
