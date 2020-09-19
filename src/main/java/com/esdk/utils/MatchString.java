package com.esdk.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.esdk.esdk;

public class MatchString {
	private List matchList = new ArrayList();
	private String _src;
	private char leftBracket;
	private char rightBracket;

	public MatchString(String str) {
		this._src = str;
		leftBracket = '(';
		rightBracket = ')';
		match();
	}

	public MatchString(String str, char leftStr, char rightStr) {
		this._src = str;
		this.leftBracket = leftStr;
		this.rightBracket = rightStr;
		match();
	}

	private void match() {
		Stack<Integer> stack = new Stack<Integer>();
		int k = 0;
		for (int i = 0; i < _src.length(); i++) {
			int index = -1;
			char c = _src.charAt(i);
			if (c == leftBracket) {
				stack.add(i);
			}
			if (c == rightBracket) {
				index = stack.pop();
				if (stack.isEmpty()) {
					String subString = _src.substring(index, i + 1);
					if (!_src.substring(k, index).equals("")) {
						matchList.add(_src.substring(k, index));
					}
					k = i + 1;
					int len = subString.length();
					String contentStr = subString.substring(1, len - 1);
					matchList.add(new MatchString(contentStr,leftBracket,rightBracket));
				}
			}
		}
		if (k < _src.length()) {
			matchList.add(_src.substring(k, _src.length()));
		}
	}
	
	@Override public String toString(){
		StringBuilder result=new StringBuilder();
		if(leftBracket!='\0')
			result.append(leftBracket);
		for(Iterator iter=matchList.iterator();iter.hasNext();){
			result.append(iter.next().toString());
		}
		if(rightBracket!='\0')
			result.append(rightBracket);
		return result.toString();
	}

	public List getMatchList() {
		return matchList;
	}

	public static void main(String[] args) {
		// String sql = "DROP VIEW IF EXISTS case_referral_view;"
		// +"create view case_referral_view as"
		// +"select cr.id AS id,cr.service_type AS service_type,cr.org_type AS org_type,cr.org_name AS org_name,cr.referral_name AS referral_name,cr.referral_tel AS referral_tel,cr.referral_date AS referral_date,cr.referral_member_name AS referral_member_name,cr.handle_deadline AS handle_deadline,cr.first_interview_date AS first_interview_date,cr.center_id AS center_id,cr.valid AS valid,cr.create_time AS create_time,cr.create_user_id AS create_user_id,cr.create_user_name AS create_user_name,d.name AS service_type_name,cr.pic_id AS pic_id,pu.name AS pic_name,c.case_id AS case_id,c.member_id AS member_id,c.case_state AS case_state,c.case_code AS case_code,c.accept_service_reason AS accept_service_reason,c.first_recommend_handle AS first_recommend_handle,c.last_recommend_handle AS last_recommend_handle,c.refuse_service_reson AS refuse_service_reson,c.predict_enter_service_date AS predict_enter_service_date,c.leave_service_date AS leave_service_date,c.leave_service_reson AS leave_service_reson,c.assess_report AS assess_report,c.assess1_date AS assess1_date,c.assess2_date AS assess2_date,c.assess3_date AS assess3_date,c.assess4_date AS assess4_date,c.assess_report_other_str AS assess_report_other_str,c.family_member_data AS family_member_data,c.ever_accept_service AS ever_accept_service,c.accepting_service AS accepting_service,c.habitation_type AS habitation_type,c.habitation_equipment AS habitation_equipment,c.habitation_toilet AS habitation_toilet,c.habitation_community AS habitation_community,c.medical_record AS medical_record,c.fracture_position AS fracture_position,c.hearing_test_date AS hearing_test_date,c.hearing_for_left AS hearing_for_left,c.hearing_for_right AS hearing_for_right,c.visual_for_left AS visual_for_left,c.visual_for_right AS visual_for_right,c.visual_test_date AS visual_test_date,c.medical_record_other_str1 AS medical_record_other_str1,c.medical_record_other_str2 AS medical_record_other_str2,c.subsequent_visit_data AS subsequent_visit_data,c.kill_oneself_record AS kill_oneself_record,c.kill_oneself_record_other_str AS kill_oneself_record_other_str,c.disease_character AS disease_character,c.accepting_drug_name AS accepting_drug_name,c.supervise AS supervise,c.supervise_other_str AS supervise_other_str,c.binge_drink AS binge_drink,c.binge_drink_other_str AS binge_drink_other_str,c.food_allergy AS food_allergy,c.food_allergy_response AS food_allergy_response,c.medicine_allergy AS medicine_allergy,c.medicine_allergy_response AS medicine_allergy_response,c.other_allergy AS other_allergy,c.other_allergy_response AS other_allergy_response,c.allergy_record AS allergy_record,c.look_after_ability_oneself AS look_after_ability_oneself,c.other_assess_item AS other_assess_item,c.other_assess1_date AS other_assess1_date,c.other_assess2_date AS other_assess2_date,c.other_assess3_date AS other_assess3_date,c.other_assess4_date AS other_assess4_date,c.other_assess_other_str AS other_assess_other_str,c.case_analysis AS case_analysis,c.actual_move_in_date_start AS actual_move_in_date_start,c.actual_move_in_date_end AS actual_move_in_date_end,c.temporary_treatment_service_reson AS temporary_treatment_service_reson,c.app_time AS app_time,c.app_user_id AS app_user_id,c.app_user_name AS app_user_name,c.is_agree AS is_agree,c.executive_opinion AS executive_opinion,c.audit_time AS audit_time,c.audit_user_id AS audit_user_id,c.audit_user_name AS audit_user_name,c.valid AS case_valid,c.close_case_date AS close_case_date,c.open_case_date AS open_case_date,c.reject_time AS reject_time,c.reject_user_id AS reject_user_id,c.medical_diagnosis AS medical_diagnosis,mv.code AS member_code,mv.name AS name,mv.chinese_name AS chinese_name,mv.english_name AS english_name,mv.sex AS sex,mv.id_card AS id_card,mv.other_id_card AS other_id_card,mv.birthday AS birthday,mv.language AS language,mv.religion AS religion,mv.marry_status AS marry_status,mv.tel1 AS tel1,mv.tel2 AS tel2,mv.mobile1 AS mobile1,mv.mobile2 AS mobile2,mv.work_tel AS work_tel,mv.is_become_member AS is_become_member,mv.portrait_path AS portrait_path,mv.body_photo_path AS body_photo_path,mv.address1 AS address1,mv.address1_a AS address1_a,mv.address1_b AS address1_b,mv.address1_c AS address1_c,mv.address1_d AS address1_d,mv.address2 AS address2,mv.address2_a AS address2_a,mv.address2_b AS address2_b,mv.address2_c AS address2_c,mv.address2_d AS address2_d,mv.guardian_name AS guardian_name,mv.guardian_sex AS guardian_sex,mv.guardian_id_card AS guardian_id_card,mv.guardian_age AS guardian_age,mv.guardian_relation AS guardian_relation,mv.guardian_email AS guardian_email,mv.guardian_daytime_tel AS guardian_daytime_tel,mv.guardian_night_tel AS guardian_night_tel,mv.guardian_mobile AS guardian_mobile,mv.guardian_address1 AS guardian_address1,mv.guardian_address1_a AS guardian_address1_a,mv.guardian_address1_b AS guardian_address1_b,mv.guardian_address1_c AS guardian_address1_c,mv.guardian_address1_d AS guardian_address1_d,mv.guardian_address2 AS guardian_address2,mv.guardian_address2_a AS guardian_address2_a,mv.guardian_address2_b AS guardian_address2_b,mv.guardian_address2_c AS guardian_address2_c,mv.guardian_address2_d AS guardian_address2_d,mv.sex_name AS sex_name,mv.member_group AS member_group,mv.age AS age,mv.guardian_birthday AS guardian_birthday,cr.crs_rehab_no AS crs_rehab_no,cr.file_no AS file_no,(select top 1 dict.name from dict where ((dict.content = c.accept_service_reason) and (dict.center_id = c.center_id) and (dict.category = 'use_service_reason'))) AS accept_service_reason_name,(select top 1 dict.name from dict where ((dict.content = c.temporary_treatment_service_reson) and (dict.center_id = c.center_id) and (dict.category = 'residential_reason'))) AS temporary_treatment_service_reson_name from ((((case_referral cr left join case_app c on((cr.id = c.referral_id))) left join dict1 d on(((cr.service_type = d.code) and (d.category = 'service_type') and (d.center_id = cr.center_id)))) left join project_user pu on((cr.pic_id = pu.user_id))) left join member_view mv on((mv.member_id = c.member_id)));";
		//String sql = "select top 1 dict.name from dict where ((dict.content = c.temporary_treatment_service_reson) and (dict.center_id = c.center_id) and (dict.category = 'residential_reason')) aa";
		String sql = "select ((top 1) dict.name) from dict";
		MatchString sm = new MatchString(sql);
		// sm.getMatchList();
		esdk.tool.assertEquals(sm.toString(),"(select ((top 1) dict.name) from dict)");
		MatchString sm1=new MatchString("name=mike and (id=3 or id <>4)");
		esdk.tool.assertEquals(sm1.toString(),"(name=mike and (id=3 or id <>4))");
	}
}
