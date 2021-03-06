package com.web.alljobgo.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.web.alljobgo.api.service.MappingService;
import com.web.alljobgo.object.ResultType;
import com.web.alljobgo.train.domain.SearchVO;
import com.web.alljobgo.train.domain.WishVO;
import com.web.alljobgo.train.service.HrdSearchService;
import com.web.alljobgo.user.domain.userVO;
import com.web.alljobgo.user.service.HrdUserService;

@Controller
@RequestMapping("/api")
public class ApiController {
	private final static Logger logger = LoggerFactory.getLogger(ApiController.class);
	
	private final HrdSearchService hrdSearchService;
	private final HrdUserService hrdUserService;
	private final MappingService mappingService;
	
	public ApiController(
			@Qualifier("searchSubject") HrdSearchService hrdSearchService,
			@Qualifier("HrdUserService") HrdUserService hrdUserService,
			@Qualifier("DataMapperService") MappingService mappingService
			) {
		this.hrdSearchService = hrdSearchService;
		this.hrdUserService = hrdUserService;
		this.mappingService = mappingService;
	}
	
	@GetMapping(value = "/hrd", consumes = MediaType.TEXT_XML_VALUE, produces = MediaType.TEXT_XML_VALUE + "; charset=utf8")
	@ResponseBody
	public String getAPIData(Model model, SearchVO searchVO) throws Exception {
		return hrdSearchService.search(searchVO);
	}
	
	@PostMapping(value = "/user/checkEmail", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String isDuplicate(@RequestBody String userEmail) throws Exception {
		logger.info("????????? ?????? ?????? => {}",userEmail);
		return hrdUserService.isExistByEmailJson(userEmail).toJSONString();
	}
	
	@GetMapping(value = "/data/event-mapping/sign", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getSignEventMap() throws Exception{
		logger.info("signEventMap!");
		return mappingService.getSignEvent().toJSONString();
	}
	
	@GetMapping(value = "/wish", produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf8")
	@ResponseBody
	public String getWishList(
			@RequestParam(value = "trainId", required = false) List<String> now,
			Authentication auth
			) throws Exception {
		logger.info("????????? ???????????? => {}", now.toString());
		
		if(auth != null) {
			logger.info("????????? ?????? ????????? ?????? ??????!");
			userVO vo = (userVO) auth.getPrincipal();
			return hrdSearchService.getUserWish(now, vo.getId()).toJSONString();
		}
		return new ResultType(false, "????????? ??????!").getJsonFormat().toJSONString();
	}
	
	@PostMapping(value = "/wish", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf8")
	@ResponseBody
	public String wishInsert(
			@RequestBody WishVO wishVO,
			Authentication auth
			) throws Exception {
		logger.info("????????? ?????? => {}", wishVO.toString());
		userVO userVO = (userVO)auth.getPrincipal();
		wishVO.setId(userVO.getId());
		return hrdSearchService.insertWish(wishVO).toJSONString();
	}
	
	@DeleteMapping(value = "/wish", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE + "; charset=utf8")
	@ResponseBody
	public String wishDelete(
			@RequestBody WishVO wishVO,
			Authentication auth
			) throws Exception {
		logger.info("????????? ?????? => {}", wishVO.toString());
		userVO userVO = (userVO)auth.getPrincipal();
		wishVO.setId(userVO.getId());
		return hrdSearchService.deleteWish(wishVO).toJSONString();
	}
}
