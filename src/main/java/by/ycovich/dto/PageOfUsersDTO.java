package by.ycovich.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageOfUsersDTO {
    private List<UserDTO> content;
    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
