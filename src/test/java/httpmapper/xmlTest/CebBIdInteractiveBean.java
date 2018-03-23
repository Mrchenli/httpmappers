package httpmapper.xmlTest;

import lombok.Data;

import java.util.List;

/**
 * 1.3.	提款前校验
 */
@Data
public class CebBIdInteractiveBean {

	private String CifClientId;//接入方客户标识号
	private String MakeLoanState;//放款状态
	private String MakeLoanAmount;//可提款金额
	private String PromptMessage;//提示信息
	private List<CebBIdInteractiveBeanList> list;

	private String LoanNo;//贷款账号
	private String ModeOfRepayment;//还款方式
	private String LoansTo;//贷款投向
	private String UseOfProceeds;//贷款用途
	private String PayDay;//还款日

	@Data
	public static class CebBIdInteractiveBeanList {
		private String LoanDateLineUnit;//贷款期限单位（D 天M 月P 旬Y 年）
		private String LoanDateLine;//贷款期限
		private String StrikeRate;//执行利率（日利率）（优惠后利率）
		private String BaseRate;//基准利率（日利率）（优惠前利率）
	}

}
