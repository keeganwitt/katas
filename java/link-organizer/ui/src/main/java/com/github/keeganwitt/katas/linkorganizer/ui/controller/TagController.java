package com.github.keeganwitt.katas.linkorganizer.ui.controller;

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

@Controller
public class TagController extends BaseController {
    @Value("${baseApiUrl}")
    String baseApiUrl;

    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    public ModelAndView tags() {
        Client<Tag> client = createClientFactory().create(Tag.class);
        Iterable<Tag> tags = client.getAll();
        ModelAndView mav = new ModelAndView("tag-list");
        mav.addObject("tags", tags);
        return mav;
    }

    @RequestMapping(value = "/tags/add", method = RequestMethod.GET)
    public ModelAndView addTag() {
        ModelAndView mav = new ModelAndView("tag-add");
        mav.addObject("tag", new Tag());
        return mav;
    }

    @RequestMapping(value = "/tags/add", method = RequestMethod.POST)
    public ModelAndView addTag(@ModelAttribute Tag tag) {
        Client<Tag> client = createClientFactory().create(Tag.class);
        client.post(tag);
        return new ModelAndView("redirect:/tags");
    }

    @RequestMapping(value = "/tags/delete/{id}", method = RequestMethod.GET)
    public ModelAndView deleteTag(@PathVariable String id) throws URISyntaxException {
        Client<Tag> client = createClientFactory().create(Tag.class);
        client.delete(new URI(baseApiUrl + "/tags/" + id));
        return new ModelAndView("redirect:/tags");
    }

    @RequestMapping(value = "/tags/edit/{id}", method = RequestMethod.GET)
    public ModelAndView editTag(@PathVariable String id) throws URISyntaxException {
        Client<Tag> client = createClientFactory().create(Tag.class);
        Tag tag = client.get(new URI(baseApiUrl + "/tags/" + id));
        ModelAndView mav = new ModelAndView("tag-edit");
        mav.addObject("tag", tag);
        return mav;
    }

    @RequestMapping(value = "/tags/edit", method = RequestMethod.POST)
    public ModelAndView editTag(@ModelAttribute Tag tag) {
        Client<Tag> client = createClientFactory().create(Tag.class);
        client.post(tag);
        return new ModelAndView("redirect:/tags");
    }
}
