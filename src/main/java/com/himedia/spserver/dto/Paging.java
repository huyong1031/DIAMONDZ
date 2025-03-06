package com.himedia.spserver.dto;

import lombok.Data;

@Data
public class Paging {
    private int page =1;
    private int totalCount;
    private int beginPage;
    private int endPage;
    private int displayRow=5;
    private int displayPage=5;
    private boolean prev;
    private boolean next;
    private int startNum;
    private int endNum;
    public void calPaging() {
        endPage = ( (int)Math.ceil( page/(double)displayPage ) ) * displayPage;
        beginPage = endPage - (displayPage - 1);
        int totalPage = (int)Math.ceil( totalCount/(double)displayRow );
        if(totalPage<endPage){
            endPage = totalPage;
            next = false;
        }else{
            next = true;
        }
        prev = (beginPage==1)?false:true;
        startNum = (page-1)*displayRow;
        endNum = page*displayRow;
        if( totalCount < endNum ){ endNum = totalCount; }
        System.out.println(beginPage + " " + endPage + " " + startNum + " " + endNum + " " + totalCount + " " + totalPage);
    }
}
