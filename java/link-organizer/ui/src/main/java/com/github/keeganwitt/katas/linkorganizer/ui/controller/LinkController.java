package com.github.keeganwitt.katas.linkorganizer.ui.controller;

import com.github.keeganwitt.katas.linkorganizer.ui.model.Link;
import com.github.keeganwitt.katas.linkorganizer.ui.model.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import uk.co.blackpepper.bowman.Client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LinkController extends BaseController {
    @Value("${baseApiUrl}")
    String baseApiUrl;

    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("redirect:/links");
    }

    @RequestMapping(value = "/links", method = RequestMethod.GET)
    public ModelAndView links() {
        Client<Link> client = createClientFactory().create(Link.class);
        Iterable<Link> allLinks = client.getAll();

        List<Map<String, String>> links = new ArrayList<>();
        for (Link l : allLinks) {
            Map<String, String> link = new HashMap<>();
            link.put("id", l.getId().getPath().substring(l.getId().getPath().lastIndexOf("/") + 1));
            link.put("url", l.getUrl());
            link.put("tags", String.join(", ", l.getTags().stream().map(Tag::getName).collect(Collectors.toSet())));
            links.add(link);
        }
        ModelAndView mav = new ModelAndView("link-list");
        mav.addObject("links", links);
        return mav;
    }

    @RequestMapping(value = "/links/add", method = RequestMethod.GET)
    public ModelAndView addLink() {
        ModelAndView mav = new ModelAndView("link-add");
        mav.addObject("link", new Link());
        return mav;
    }

    @RequestMapping(value = "/links/add", method = RequestMethod.POST)
    public ModelAndView addLink(@ModelAttribute Link link) {
        Client<Link> client = createClientFactory().create(Link.class);
        client.post(link);
        return new ModelAndView("redirect:/links");
    }

    @RequestMapping(value = "/links/delete/{id}", method = RequestMethod.GET)
    public ModelAndView deleteLink(@PathVariable String id) throws URISyntaxException {
        Client<Link> client = createClientFactory().create(Link.class);
        client.delete(new URI(baseApiUrl + "/links/" + id));
        return new ModelAndView("redirect:/links");
    }
}
