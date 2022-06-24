package nextstep.subway.path.ui;

import nextstep.subway.auth.domain.AuthenticationPrincipal;
import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.exceptions.SourceAndTargetSameException;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {
    private final PathService pathService;

    public PathController(final PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PathResponse> getShortestPaths(
            @RequestParam(value = "source") final Long source
            , @RequestParam(value = "target") final Long target
            , @AuthenticationPrincipal(required = false) LoginMember loginMember) {
        if (source.equals(target)) {
            throw new SourceAndTargetSameException();
        }
        return ResponseEntity.ok().body(pathService.getShortestPaths(source, target, loginMember));
    }
}
